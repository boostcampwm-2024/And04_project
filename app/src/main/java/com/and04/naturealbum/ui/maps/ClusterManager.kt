package com.and04.naturealbum.ui.maps

import android.graphics.PointF
import androidx.annotation.IntRange
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import com.and04.naturealbum.R
import com.naver.maps.map.NaverMap
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.DefaultMarkerManager
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.clustering.MarkerInfo
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

class ClusterManager(
    private val colorRange: ColorRange,
    private val onClusterClick: (MarkerInfo) -> Overlay.OnClickListener,
    private val onClusterChange: (MarkerInfo) -> Unit
) {
    private var uid: String = ""
    private val cluster: Clusterer<PhotoKey>

    init {
        cluster = Clusterer.ComplexBuilder<PhotoKey>().thresholdStrategy { _ ->
            THRESHOLD_DISTANCE
        }.tagMergeStrategy { cluster ->
            cluster.children.flatMap { node -> node.tag as List<PhotoItem> }
        }.clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                updateMarker(info, marker)
            }
        }).leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                updateMarker(info, marker)
            }
        }).markerManager(object : DefaultMarkerManager() {
            override fun createMarker(): Marker {
                return Marker().apply {
                    globalZIndex = DEFAULT_MARKER_Z_INDEX - 1
                    icon = markerIcon
                    isFlat = true
                    anchor = PointF(0.5f, 0.5f)
                    setCaptionAligns(Align.Center, Align.Center)
                    captionTextSize = 24f
                }
            }
        }).build()
    }

    private fun updateMarker(info: MarkerInfo, marker: Marker) {
        onClusterChange(info)

        val size = when (info) {
            is ClusterMarkerInfo -> info.size
            is LeafMarkerInfo -> LEAF_NODE_SIZE
            else -> throw IllegalArgumentException("Invalid marker info")
        }

        marker.zIndex = size
        marker.captionText = size.toString()
        marker.iconTintColor = sizeToTint(size)
        marker.onClickListener = onClusterClick(info)
    }

    fun setPhotoItems(uid: String, photoItems: List<PhotoItem>) {
        if (this.uid != uid) {
            cluster.clear()
            this.uid = uid
        }
        cluster.addAll(photoItems.associate { photoItem ->
            PhotoKey(
                photoItem
            ) to listOf(photoItem)
        })
    }

    fun setMap(map: NaverMap?) {
        cluster.map = map
    }

    fun clear() {
        cluster.clear()
        setMap(null)
    }

    private fun sizeToTint(size: Int): Int = sizeToTint(size, colorRange.min, colorRange.max)

    companion object {
        private const val LEAF_NODE_SIZE = 1
        private const val DEFAULT_MARKER_Z_INDEX = 200000
        private const val THRESHOLD_DISTANCE = 30.0

        private val markerIcon = OverlayImage.fromResource(R.drawable.ic_cluster)

        fun getList(
            bottomSheetPhotos: MutableState<List<PhotoItem>>,
            pick: MutableState<PhotoItem?>
        ): List<ClusterManager> =
            ColorRange.entries.map { colorRange ->
                ClusterManager(
                    colorRange = colorRange,
                    onClusterClick = { info ->
                        Overlay.OnClickListener {
                            bottomSheetPhotos.value = info.tag as List<PhotoItem>
                            pick.value = bottomSheetPhotos.value
                                .groupBy { photoItem -> photoItem.label }
                                .maxBy { (_, photoItems) -> photoItems.size }.value
                                .maxBy { photoItem -> photoItem.time }
                            true
                        }
                    },
                    onClusterChange = { info ->
                        val changedCluster = info.tag as List<PhotoItem>
                        if (changedCluster.contains(pick.value)) bottomSheetPhotos.value =
                            changedCluster
                    }
                )
            }


        private fun sizeToTint(
            size: Int, min: Color, max: Color, @IntRange(from = 1) threshold: Int = 20
        ): Int = lerp(min, max, size / threshold.toFloat()).toArgb()
    }
}

enum class ColorRange(val min: Color, val max: Color) {
    RED(Color(0xFF440000), Color(0xFFFF0000)),
    YELLOW(Color(0xFF444400), Color(0xFFFFFF00)),
    GREEN(Color(0xFF004400), Color(0xFF00FF00)),
    ORANGE(Color(0xFF442200), Color(0xFFFF8800)),
    BLUE(Color(0xFF000044), Color(0xFF0000FF)),
}
