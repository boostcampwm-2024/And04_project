package com.and04.naturealbum.ui.maps

import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.utils.time.toLocalDateTime
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import java.time.LocalDateTime

class PhotoKey(photoItem: PhotoItem) : ClusteringKey {
    val id = photoItem.uri.hashCode()
    private val position = photoItem.position
    override fun getPosition(): LatLng = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhotoKey) return false
        return id == other.id
    }

    override fun hashCode() = id
}

data class LabelItem(
    val name: String,
    val color: String,
)

data class PhotoItem(
    val uri: String,
    val position: LatLng,
    val label: LabelItem,
    val time: LocalDateTime,
)

// Room Data -> UI Data
fun Label.toLabelItem() = LabelItem(name, backgroundColor)
fun List<PhotoDetail>.toPhotoItems(labels: List<Label>): List<PhotoItem> {
    val labelMap = labels.associate { roomLabel -> roomLabel.id to roomLabel.toLabelItem() }
    return map { photoDetail ->
        PhotoItem(
            photoDetail.photoUri,
            LatLng(photoDetail.latitude, photoDetail.longitude),
            labelMap.getValue(photoDetail.labelId),
            photoDetail.datetime
        )
    }
}

// FireBase Data -> UI Data
fun FirebaseLabelResponse.toLabelItem() = LabelItem(labelName, backgroundColor)
fun List<FirebasePhotoInfoResponse>.toFriendPhotoItems(labels: List<FirebaseLabelResponse>): List<PhotoItem> {
    val labelMap =
        labels.associate { firebaseLabel -> firebaseLabel.labelName to firebaseLabel.toLabelItem() }
    return mapNotNull { firebasePhotoInfo ->
        labelMap[firebasePhotoInfo.label]?.let { label ->
            PhotoItem(
                firebasePhotoInfo.uri,
                LatLng(
                    firebasePhotoInfo.latitude ?: return@let null,
                    firebasePhotoInfo.longitude ?: return@let null
                ),
                label,
                firebasePhotoInfo.datetime.toLocalDateTime()
            )
        }
    }
}
