package com.and04.naturealbum.data.localdata.room

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "label")
data class Label(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "background_color") val backgroundColor: String,
    @ColumnInfo(name = "name") val name: String,
) : Parcelable {
    companion object {
        private const val DUMMY_LABEL_BACKGROUND_COLOR = "FFFFFF"
        private const val DUMMY_LABEL_NAME = "빈 라벨"
        const val NEW_LABEL = 0

        fun emptyLabel(): Label {
            return Label(
                id = 0,
                backgroundColor = DUMMY_LABEL_BACKGROUND_COLOR,
                name = DUMMY_LABEL_NAME
            )
        }
    }
}

@Entity(
    tableName = "album",
    foreignKeys = [
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["label_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PhotoDetail::class,
            parentColumns = ["id"],
            childColumns = ["photo_detail_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Album(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "label_id") val labelId: Int,
    @ColumnInfo(name = "photo_detail_id") val photoDetailId: Int,
)

@Entity(
    tableName = "photo_detail",
    foreignKeys = [
        ForeignKey(
            entity = Label::class,
            parentColumns = ["id"],
            childColumns = ["label_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

@Immutable
data class PhotoDetail(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "label_id") val labelId: Int,
    @ColumnInfo(name = "photo_uri") val photoUri: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "datetime") val datetime: LocalDateTime,
    @ColumnInfo(name = "hazard_check_result") val hazardCheckResult: HazardAnalyzeStatus,
    @ColumnInfo(name = "address") val address: String,
) {
    companion object {
        fun emptyPhotoDetail(): PhotoDetail {
            return PhotoDetail(
                id = 0,
                labelId = 0,
                photoUri = "",
                fileName = "",
                longitude = 0.0,
                latitude = 0.0,
                description = "",
                datetime = LocalDateTime.now(),
                hazardCheckResult = HazardAnalyzeStatus.NOT_CHECKED,
                address = ""
            )
        }
    }
}

enum class HazardAnalyzeStatus {
    PASS,       // 통과
    FAIL,       // 불통과
    NOT_CHECKED  // 검사하지 않음
}
