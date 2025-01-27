package com.and04.naturealbum.ui.album.labelphotos

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.model.AlbumFolderData
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.component.PermissionDialogState
import com.and04.naturealbum.ui.component.PermissionDialogs
import com.and04.naturealbum.ui.component.RotatingImageLoading
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.ui.utils.UiState
import com.and04.naturealbum.utils.GetTopBar
import com.and04.naturealbum.utils.color.toColor
import com.and04.naturealbum.utils.gridColumnCount

@Composable
fun AlbumFolderScreen(
    selectedAlbumLabel: Int = 0,
    onPhotoClick: (Int) -> Unit,
    onNavigateToMyPage: () -> Unit,
    navigateToBackScreen: () -> Unit,
    onNavigateToAlbum: () -> Unit,
    state: AlbumFolderState = rememberAlbumFolderState(),
    albumFolderViewModel: AlbumFolderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val uiState = albumFolderViewModel.uiState.collectAsStateWithLifecycle()

    val setLoading = { isImgDownLoading: Boolean -> state.imgDownLoading.value = isImgDownLoading }
    val switchEditMode = { isEditModeEnabled: Boolean ->
        state.editMode.value = isEditModeEnabled
    }

    if (uiState.value is UiState.Idle) {
        albumFolderViewModel.loadFolderData(selectedAlbumLabel)
    }

    val saveImagesWithLoading = {
        saveImagesWithLoading(
            context = context,
            photoDetails = state.checkList.value.toList(),
            setLoading = setLoading,
            switchEditMode = switchEditMode,
        )
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission())
        { permissionAllow ->
            if (permissionAllow) {
                saveImagesWithLoading()
            } else {
                val activity = context as? Activity ?: return@rememberLauncherForActivityResult
                val hasPreviouslyDeniedPermission =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        WRITE_EXTERNAL_STORAGE
                    )
                if (!hasPreviouslyDeniedPermission)
                    state.permissionDialogState.value = PermissionDialogState(
                        onDismiss = { state.permissionDialogState.value = null },
                        onConfirmation = {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                                context.startActivity(this)
                            }
                        },
                        dialogText = R.string.album_folder_permission_go_to_settings
                    )
            }
        }

    val requestPermission = { requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE) }

    val savePhotos = {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            requestPermission()
        } else {
            saveImagesWithLoading()
        }
    }

    val deletePhotos: () -> Unit = {
        albumFolderViewModel.deletePhotos(state.checkList.value)
        switchEditMode(false)
    }

    AlbumFolderScreen(
        uiState = uiState,
        onPhotoClick = onPhotoClick,
        switchEditMode = switchEditMode,
        editMode = state.editMode,
        selectAll = state.selectAll,
        savePhotos = savePhotos,
        deletePhotos = deletePhotos,
        onNavigateToMyPage = onNavigateToMyPage,
        navigateToBackScreen = navigateToBackScreen,
        onNavigateToAlbum = onNavigateToAlbum,
        checkList = state.checkList,
    )

    if (state.imgDownLoading.value) {
        RotatingImageLoading(
            drawableRes = R.drawable.fish_loading_image,
            stringRes = R.string.album_folder_screen_save_text
        )
    }

    PermissionDialogs(state.permissionDialogState.value)
}

@Composable
fun AlbumFolderScreen(
    uiState: State<UiState<AlbumFolderData>>,
    onPhotoClick: (Int) -> Unit,
    switchEditMode: (Boolean) -> Unit,
    editMode: MutableState<Boolean>,
    selectAll: MutableState<Boolean>,
    savePhotos: () -> Unit,
    deletePhotos: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    navigateToBackScreen: () -> Unit,
    checkList: MutableState<Set<PhotoDetail>>,
    onNavigateToAlbum: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            context.GetTopBar(
                type = AppBarType.All,
                navigateToBackScreen = navigateToBackScreen,
                navigateToMyPage = onNavigateToMyPage,
            )
        }
    ) { innerPadding ->
        ItemContainer(
            innerPaddingValues = innerPadding,
            uiState = uiState,
            onPhotoClick = onPhotoClick,
            switchEditMode = switchEditMode,
            editMode = editMode,
            selectAll = selectAll,
            savePhotos = savePhotos,
            deletePhotos = deletePhotos,
            checkList = checkList,
            onNavigateToAlbum = onNavigateToAlbum,
        )
    }
}

@Composable
private fun ItemContainer(
    innerPaddingValues: PaddingValues,
    uiState: State<UiState<AlbumFolderData>>,
    onPhotoClick: (Int) -> Unit,
    switchEditMode: (Boolean) -> Unit,
    editMode: MutableState<Boolean>,
    selectAll: MutableState<Boolean>,
    savePhotos: () -> Unit,
    deletePhotos: () -> Unit,
    checkList: MutableState<Set<PhotoDetail>>,
    onNavigateToAlbum: () -> Unit,
) {
    if (uiState.value is UiState.Success) {
        val success = (uiState.value as UiState.Success)
        val label = success.data.label
        val photoDetails = success.data.photoDetails

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumLabel(
                    modifier = Modifier
                        .background(
                            color = label.backgroundColor.toColor(),
                            shape = CircleShape
                        )
                        .fillMaxWidth(0.9f),
                    text = label.name,
                    backgroundColor = label.backgroundColor.toColor(),
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(LocalContext.current.gridColumnCount()),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalItemSpacing = 16.dp
                    ) {
                        items(
                            items = photoDetails,
                            key = { item -> item.id }
                        ) { photoDetail ->
                            PhotoDetailItem(
                                photoDetail = photoDetail,
                                onPhotoClick = onPhotoClick,
                                switchEditMode = switchEditMode,
                                editMode = editMode,
                                selectAll = selectAll.value,
                                checkList = checkList,
                            )
                        }
                    }

                    ButtonWithAnimation(
                        selectAll = { isAllSelected: Boolean ->
                            selectAll.value = isAllSelected
                            if (isAllSelected)
                                checkList.value = photoDetails.toSet()
                            else checkList.value = emptySet()
                        },
                        savePhotos = savePhotos,
                        deletePhotos = deletePhotos,
                        editMode = editMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }

        BackHandler(enabled = editMode.value) {
            if (editMode.value)
                editMode.value = false
            checkList.value = setOf()
        }
    }
    if (uiState.value is UiState.Error<*>) {
        onNavigateToAlbum()
    }
}

@Composable
private fun PhotoDetailItem(
    photoDetail: PhotoDetail,
    onPhotoClick: (Int) -> Unit,
    switchEditMode: (Boolean) -> Unit,
    editMode: State<Boolean>,
    selectAll: Boolean,
    checkList: MutableState<Set<PhotoDetail>>,
) {
    var isSelected by rememberSaveable { mutableStateOf(selectAll) }
    LaunchedEffect(selectAll) { isSelected = selectAll }
    if (!editMode.value) isSelected = false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isSelected = true
                        switchEditMode(true)
                        checkList.value += photoDetail
                    },
                    onTap = {
                        if (editMode.value) {
                            isSelected = !isSelected
                            if (isSelected) {
                                checkList.value += photoDetail
                            } else {
                                checkList.value -= photoDetail
                            }
                        } else {
                            onPhotoClick(photoDetail.id)
                        }
                    })
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .fillMaxWidth()
            ) {
                PhotoDetailImage(photoDetail = photoDetail)
                if (isSelected) {
                    ImageOverlay(
                        modifier = Modifier
                            .matchParentSize()

                    )
                }
            }
            Text(
                text = photoDetail.description,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PhotoDetailImage(photoDetail: PhotoDetail) {
    val rememberedImage = rememberSaveable(photoDetail.photoUri) { photoDetail.photoUri }
    val time = photoDetail.datetime
    AsyncImage(
        model = rememberedImage,
        contentDescription = stringResource(R.string.album_folder_screen_item_image_description),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ImageOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(R.string.album_folder_screen_item_select_icon_description),
            tint = MaterialTheme.colorScheme.surface,
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun AlbumFolderScreenPreview() {
    NatureAlbumTheme {
        val uiState = remember {
            mutableStateOf(
                UiState.Success(
                    AlbumFolderData(
                        Label.emptyLabel(),
                        listOf()
                    )
                )
            )
        }
        val editMode = remember { mutableStateOf(false) }
        val selectAll = remember { mutableStateOf(false) }
        val checkList = remember { mutableStateOf<Set<PhotoDetail>>(setOf()) }

        AlbumFolderScreen(
            uiState = uiState,
            onPhotoClick = { },
            switchEditMode = { _ -> },
            editMode = editMode,
            selectAll = selectAll,
            savePhotos = { },
            deletePhotos = {},
            onNavigateToMyPage = { },
            navigateToBackScreen = { },
            checkList = checkList,
            onNavigateToAlbum = {}
        )
    }
}
