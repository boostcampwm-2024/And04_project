package com.and04.naturealbum.data.localdata.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM label")
    suspend fun getAllLabel(): List<Label>

    @Query("SELECT name FROM label")
    suspend fun getAllNameOfLabel(): List<String>

    @Query("SELECT * FROM label WHERE id = :id")
    suspend fun getLabelById(id: Int): Label

    @Query("SELECT id FROM label WHERE name = :name")
    suspend fun getIdByName(name: String): Int?

    @Query("SELECT background_color FROM label WHERE id = :id")
    suspend fun getLabelBackgroundColorById(id: Int): String

    @Query("SELECT name FROM label WHERE id = :id")
    suspend fun getLabelNameById(id: Int): String

    @Insert
    suspend fun insertLabel(label: Label): Long
}

@Dao
interface AlbumDao {
    @Insert
    suspend fun insertAlbum(album: Album): Long

    @Update
    suspend fun updateAlbum(album: Album)

    @Query(
        """
        SELECT
            label.id AS labelId,
            label.name AS labelName,
            label.background_color AS labelBackgroundColor,
            photo_detail.photo_uri AS photoDetailUri           
        FROM
            album
        JOIN label ON album.label_id = label.id
        JOIN photo_detail ON album.photo_detail_id = photo_detail.id
            
    """
    )
    fun getAllAlbum(): Flow<List<AlbumDto>>

    @Query(
        """
        SELECT
            label.id AS labelId,
            label.name AS labelName,
            label.background_color AS labelBackgroundColor,
            photo_detail.photo_uri AS photoDetailUri,
            photo_detail.file_name As fileName
        FROM album
        JOIN label ON album.label_id = label.id
        JOIN photo_detail ON album.photo_detail_id = photo_detail.id
    """
    )
    suspend fun getSyncCheckAlbums(): List<SyncAlbumsDto>

    @Query(
        """
        SELECT
            label.name AS labelName,
            photo_detail.photo_uri AS photoDetailUri,
            photo_detail.file_name As fileName,
            photo_detail.longitude As longitude,
            photo_detail.latitude As latitude,
            photo_detail.description As description,
            photo_detail.datetime As datetime
        FROM label
        JOIN photo_detail ON label.id == photo_detail.label_id
    """
    )
    suspend fun getSyncCheckPhotos(): List<SyncPhotoDetailsDto>

    @Query("SELECT * FROM album WHERE label_id = :labelId")
    suspend fun getAlbumByLabelId(labelId: Int): List<Album>

    @Query(
        """
        UPDATE album
        SET photo_detail_id = :photoDetailId
        WHERE label_id = (
            SELECT label_id
            FROM photo_detail
            WHERE id = :photoDetailId
        )
    """
    )
    suspend fun updateAlbumPhotoDetailByAlbumId(photoDetailId: Int)
}

@Dao
interface PhotoDetailDao {
    @Insert
    suspend fun insertPhotoDetail(photoDetail: PhotoDetail): Long

    @Query("SELECT * FROM photo_detail")
    suspend fun getAllPhotoDetail(): List<PhotoDetail>

    @Query("SELECT * FROM photo_detail WHERE id = :id")
    suspend fun getPhotoDetailById(id: Int): PhotoDetail

    @Query("SELECT photo_uri FROM photo_detail WHERE id = :id")
    suspend fun getPhotoDetailUriById(id: Int): String

    @Query("SELECT * FROM photo_detail WHERE label_id = :labelId")
    suspend fun getAllPhotoDetailsUriByLabelId(labelId: Int): List<PhotoDetail>

    @Query("SELECT hazard_check_result FROM photo_detail WHERE id = :id")
    suspend fun getHazardCheckResultById(id: Int): HazardAnalyzeStatus

    @Query("SELECT hazard_check_result FROM photo_detail WHERE file_name = :fileName")
    suspend fun getHazardCheckResultByFileName(fileName: String): HazardAnalyzeStatus

    @Query("SELECT address FROM photo_detail WHERE id = :id")
    suspend fun getAddress(id: Int): String

    @Query("UPDATE photo_detail SET hazard_check_result = :hazardAnalyzeStatus WHERE file_name = :fileName")
    suspend fun updateHazardCheckResultByFIleName(
        hazardAnalyzeStatus: HazardAnalyzeStatus,
        fileName: String,
    )

    @Query("UPDATE photo_detail SET address = :address WHERE id = :id")
    suspend fun updateAddressById(
        address: String,
        id: Int,
    )

    @Delete
    suspend fun deleteImage(photoDetail: PhotoDetail)
}
