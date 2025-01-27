package com.and04.naturealbum.data.localdata.room

import androidx.room.TypeConverter
import com.and04.naturealbum.utils.time.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun localDateTimeToString(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun stringToLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.toLocalDateTime()
    }

    @TypeConverter
    fun fromHazardCheckStatus(value: String): HazardAnalyzeStatus {
        return HazardAnalyzeStatus.valueOf(value)
    }

    @TypeConverter
    fun toHazardCheckStatus(status: HazardAnalyzeStatus): String {
        return status.name
    }
}
