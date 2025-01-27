package com.and04.naturealbum.data.repository

import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus

class TestAiRepoImpl: RetrofitRepository {
    override suspend fun convertCoordsToAddress(latitude: Double, longitude: Double): String {
        return ""
    }

    override suspend fun analyzeHazardWithGreenEye(photoData: String): HazardAnalyzeStatus {
        return HazardAnalyzeStatus.PASS
    }
}
