package com.and04.naturealbum.maps

import com.and04.naturealbum.ui.maps.LabelItem
import com.and04.naturealbum.ui.maps.PhotoItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

class MapBoundaryCalculationTest {

    private fun generateDummyPhotos(count: Int): List<PhotoItem> {
        return List(count) { index ->
            PhotoItem(
                uri = "photo_$index",
                position = LatLng(37.0 + index * 0.001, 127.0 + index * 0.001),
                label = LabelItem("Label_${index % 5}", "#FF0000"),
                time = LocalDateTime.now()
            )
        }
    }

    // 기존 방식: 2중 for문
    private fun measureOriginalBoundaryCalculationTime(photoItems: Map<String, List<PhotoItem>>): Long {
        return measureTimeMillis {
            LatLngBounds.Builder().apply {
                photoItems.values.forEach { photoList ->
                    photoList.forEach { photoItem ->
                        include(photoItem.position)
                    }
                }
            }.build()
        }
    }

    // 리팩토링 방식: 이미 flatten 해둔 totalPhotos를 사용할 경우
    private fun measureOptimizedBoundaryCalculationTime(photoItems: List<PhotoItem>): Long {
        return measureTimeMillis {
            LatLngBounds.Builder().apply {
                photoItems.forEach { photoItem ->
                    include(photoItem.position)
                }
            }.build()
        }
    }

    @Test
    fun testBoundaryCalculationPerformance() {
        val photoCounts = listOf(1000, 5000, 10000, 50000, 100000, 200000, 400000, 500000)
        val iterations = 5

        photoCounts.forEach { count ->

            val dummyPhotos = generateDummyPhotos(count)
            val photosByUid = dummyPhotos.groupBy { it.label.name } // Map 형태로 변환

            var originalTotalTime = 0L
            var optimizedTotalTime = 0L

            repeat(iterations) {
                originalTotalTime += measureOriginalBoundaryCalculationTime(photosByUid)
                optimizedTotalTime += measureOptimizedBoundaryCalculationTime(dummyPhotos)
            }

            val originalAvgTime = originalTotalTime / iterations
            val optimizedAvgTime = optimizedTotalTime / iterations

            println("사진 개수: $count")
            println("기존 방식 평균 시간: $originalAvgTime ms")
            println("리팩토링 방식 평균 시간 (단일 리스트): $optimizedAvgTime ms")
            println("---")

            assertTrue("리팩토링이 기존 방식보다 느립니다.", optimizedAvgTime <= originalAvgTime)
        }
    }
}
