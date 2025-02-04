package com.and04.naturealbum.data.repository.local.impl

import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.localdata.room.AlbumDao
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.localdata.room.LabelDao
import com.and04.naturealbum.data.localdata.room.PhotoDetailDao
import com.and04.naturealbum.data.repository.local.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao,
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao
): SyncRepository {

    override suspend fun getIdByName(name: String): Int? {
        return labelDao.getIdByName(name)
    }

    override suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto> {
        return albumDao.getSyncCheckAlbums()
    }

    override suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto> {
        return albumDao.getSyncCheckPhotos()
    }

    override suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus {
        return photoDetailDao.getHazardCheckResultByFileName(fileName)
    }

    override suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String,
    ) {
        return photoDetailDao.updateHazardCheckResultByFIleName(hazardAnalyzeStatus, fileName)
    }
}
