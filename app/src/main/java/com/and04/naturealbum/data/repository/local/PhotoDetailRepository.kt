package com.and04.naturealbum.data.repository.local

import com.and04.naturealbum.data.localdata.room.PhotoDetail

interface PhotoDetailRepository {
    suspend fun getAllPhotoDetail(): List<PhotoDetail>
    suspend fun getPhotoDetailById(id: Int): PhotoDetail
    suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>
    suspend fun deleteImage(photoDetail: PhotoDetail)
    suspend fun getAddressByPhotoDetailId(photoDetailId: Int): String
    suspend fun updateAddressByPhotoDetailId(address: String, photoDetailId: Int)
    suspend fun insertPhoto(photoDetail: PhotoDetail): Long
}
