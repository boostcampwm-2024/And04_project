package com.and04.naturealbum.data.repository.local.impl

import com.and04.naturealbum.data.localdata.room.Album
import com.and04.naturealbum.data.localdata.room.AlbumDao
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.localdata.room.PhotoDetailDao
import com.and04.naturealbum.data.repository.local.PhotoDetailRepository
import javax.inject.Inject

class PhotoDetailRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao,
    private val photoDetailDao: PhotoDetailDao,
) : PhotoDetailRepository {

    override suspend fun insertPhoto(photoDetail: PhotoDetail): Long {
        return photoDetailDao.insertPhotoDetail(photoDetail)
    }

    override suspend fun getAllPhotoDetail(): List<PhotoDetail> {
        return photoDetailDao.getAllPhotoDetail()
    }

    override suspend fun getPhotoDetailById(id: Int): PhotoDetail {
        return photoDetailDao.getPhotoDetailById(id)
    }

    override suspend fun getPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail> {
        return photoDetailDao.getAllPhotoDetailsUriByLabelId(labelId)
    }

    override suspend fun deleteImage(photoDetail: PhotoDetail) {
        val album = albumDao.getAlbumByLabelId(photoDetail.labelId).first()
        val isRepresentedImage = album.photoDetailId == photoDetail.id
        val nextRepresentedImage =
            photoDetailDao.getAllPhotoDetailsUriByLabelId(photoDetail.labelId)
                .firstOrNull { it != photoDetail }

        if (isRepresentedImage && nextRepresentedImage != null) {
            albumDao.updateAlbum(
                Album(
                    id = album.id,
                    labelId = photoDetail.labelId,
                    photoDetailId = nextRepresentedImage.id
                )
            )
        }
        return photoDetailDao.deleteImage(photoDetail)
    }

    override suspend fun getAddressByPhotoDetailId(photoDetailId: Int): String {
        return photoDetailDao.getAddress(id = photoDetailId)
    }

    override suspend fun updateAddressByPhotoDetailId(address: String, photoDetailId: Int) {
        photoDetailDao.updateAddressById(address = address, id = photoDetailId)
    }
}
