package com.and04.naturealbum.utils.time

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.toDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault())
    return format(formatter)
}

fun LocalDateTime.toSyncDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime
    .parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    .atZone(ZoneId.of("UTC"))
    .withZoneSameInstant(ZoneId.systemDefault())
    .toLocalDateTime()

fun LocalDateTime.toDateTimeString(): String = format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
