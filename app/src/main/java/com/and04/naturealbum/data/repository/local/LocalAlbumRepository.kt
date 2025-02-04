package com.and04.naturealbum.data.repository.local

import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.localdata.room.Album
import kotlinx.coroutines.flow.Flow

interface LocalAlbumRepository {
    fun getAllAlbum(): Flow<List<AlbumDto>>
    suspend fun getAlbumByLabelId(labelId: Int): List<Album>
    suspend fun updateAlbum(album: Album)
    suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int)
    suspend fun insertPhotoInAlbum(album: Album): Long
}
