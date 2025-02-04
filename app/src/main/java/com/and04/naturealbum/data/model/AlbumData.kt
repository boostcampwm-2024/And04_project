package com.and04.naturealbum.data.model

import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail

data class AlbumData(
    val label: Label,
    val photoDetails: PhotoDetail
)

data class AlbumFolderData(
    val label: Label,
    val photoDetails: List<PhotoDetail>
)
