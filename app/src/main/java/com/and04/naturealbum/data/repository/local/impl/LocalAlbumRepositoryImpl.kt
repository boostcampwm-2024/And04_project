package com.and04.naturealbum.data.repository.local.impl

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.localdata.room.Album
import com.and04.naturealbum.data.localdata.room.AlbumDao
import com.and04.naturealbum.data.repository.local.LocalAlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalAlbumRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao,
): LocalAlbumRepository {
    override fun getAllAlbum(): Flow<List<AlbumDto>> {
        return albumDao.getAllAlbum()
    }

    override suspend fun getAlbumByLabelId(labelId: Int): List<Album> {
        return albumDao.getAlbumByLabelId(labelId)
    }

    override suspend fun updateAlbum(album: Album) {
        return albumDao.updateAlbum(album)
    }

    override suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int) {
        return albumDao.updateAlbumPhotoDetailByAlbumId(photoDetailId)
    }

    override suspend fun insertPhotoInAlbum(album: Album): Long {
        return albumDao.insertAlbum(album)
    }
}
