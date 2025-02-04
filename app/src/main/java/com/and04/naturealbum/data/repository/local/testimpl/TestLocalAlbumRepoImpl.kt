package com.and04.naturealbum.data.repository.local.testimpl

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.localdata.room.Album
import com.and04.naturealbum.data.repository.local.LocalAlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestLocalAlbumRepoImpl: LocalAlbumRepository {
    override fun getAllAlbum(): Flow<List<AlbumDto>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAlbumByLabelId(labelId: Int): List<Album> {
        return emptyList()
    }

    override suspend fun updateAlbum(album: Album) {
    }

    override suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int) {
    }

    override suspend fun insertPhotoInAlbum(album: Album): Long {
        return 0
    }
}
