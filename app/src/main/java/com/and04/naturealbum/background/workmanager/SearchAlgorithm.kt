package com.and04.naturealbum.background.workmanager

import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.dto.Sync
import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto

fun List<Sync>.binarySearch(
    fromIndex: Int = 0,
    toIndex: Int = size,
    target: String
): Boolean {
    rangeCheck(size, fromIndex, toIndex)

    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midVal = when (val midSync = get(mid)) {
            is SyncAlbumsDto -> {
                midSync.labelName
            }

            is SyncPhotoDetailsDto -> {
                midSync.fileName
            }

            is FirebasePhotoInfoResponse -> {
                midSync.fileName
            }
        }
        val cmp = if (target == midVal) 0 else if (target > midVal) -1 else 1

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return true
    }
    return false
}

private fun rangeCheck(size: Int, fromIndex: Int, toIndex: Int) {
    when {
        fromIndex > toIndex -> throw IllegalArgumentException("fromIndex ($fromIndex) is greater than toIndex ($toIndex).")
        fromIndex < 0 -> throw IndexOutOfBoundsException("fromIndex ($fromIndex) is less than zero.")
        toIndex > size -> throw IndexOutOfBoundsException("toIndex ($toIndex) is greater than size ($size).")
    }
}
