package com.and04.naturealbum.data.repository.local.testimpl

import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.repository.local.SyncRepository

class TestSyncRepoImpl: SyncRepository {
    override suspend fun getIdByName(name: String): Int? {
        return null
    }

    override suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto> {
        return emptyList()
    }

    override suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto> {
        return emptyList()
    }

    override suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus {
        return HazardAnalyzeStatus.PASS
    }

    override suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String
    ) {}
}
