package com.and04.naturealbum.ui.album.labelphotos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.ui.component.PermissionDialogState

@Stable
class AlbumFolderState(
    imgDownLoading: Boolean,
    editMode: Boolean,
    checkList: Set<PhotoDetail>,
    selectAll: Boolean,
    permissionDialogState: PermissionDialogState?,
) {
    var imgDownLoading = mutableStateOf(imgDownLoading)
    var editMode = mutableStateOf(editMode)
    var checkList = mutableStateOf(checkList)
    var selectAll = mutableStateOf(selectAll)
    var permissionDialogState = mutableStateOf<PermissionDialogState?>(null)
}

@Composable
fun rememberAlbumFolderState(
    imgDownLoading: Boolean = false,
    editMode: Boolean = false,
    checkList: Set<PhotoDetail> = setOf(),
    selectAll: Boolean = false,
    permissionDialogState: PermissionDialogState? = null,
): AlbumFolderState {
    return remember {
        AlbumFolderState(
            imgDownLoading = imgDownLoading,
            editMode = editMode,
            checkList = checkList,
            selectAll = selectAll,
            permissionDialogState = permissionDialogState,
        )
    }
}
