package com.and04.naturealbum.data.repository.local.testimpl

import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.repository.local.PhotoDetailRepository

class TestPhotoDetailRepoImpl: PhotoDetailRepository {
    override suspend fun getAllPhotoDetail(): List<PhotoDetail> {
        return emptyList()
    }

    override suspend fun getPhotoDetailById(id: Int): PhotoDetail {
        return PhotoDetail.emptyPhotoDetail()
    }

    override suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail> {
        return emptyList()
    }

    override suspend fun deleteImage(photoDetail: PhotoDetail) {}

    override suspend fun getAddressByPhotoDetailId(photoDetailId: Int): String {
        return ""
    }

    override suspend fun updateAddressByPhotoDetailId(address: String, photoDetailId: Int) {

    }

    override suspend fun insertPhoto(photoDetail: PhotoDetail): Long {
        return 0
    }
}
