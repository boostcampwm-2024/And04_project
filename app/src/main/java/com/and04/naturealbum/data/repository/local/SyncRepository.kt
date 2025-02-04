package com.and04.naturealbum.data.repository.local

import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus

interface SyncRepository {
    suspend fun getIdByName(name: String): Int?
    suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto>
    suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto>
    suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus
    suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String,
    )
}
