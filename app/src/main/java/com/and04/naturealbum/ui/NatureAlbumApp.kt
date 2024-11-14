package com.and04.naturealbum.ui

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.ui.album.AlbumScreen
import com.and04.naturealbum.ui.albumfolder.AlbumFolderScreen
import com.and04.naturealbum.ui.home.HomeScreen
import com.and04.naturealbum.ui.labelsearch.LabelSearchScreen
import com.and04.naturealbum.ui.maps.MapScreen
import com.and04.naturealbum.ui.mypage.MyPageScreen
import com.and04.naturealbum.ui.photoinfo.PhotoInfo
import com.and04.naturealbum.ui.savephoto.SavePhotoScreen
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import java.io.File

@Composable
fun NatureAlbumApp() {
    val navController = rememberNavController()

    NatureAlbumTheme {
        NatureAlbumNavHost(navController)
    }
}

@Composable
fun NatureAlbumNavHost(
    navController: NavHostController,
) {
    val context = LocalContext.current
    var lastLocation: Location? by rememberSaveable { mutableStateOf(null) }
    var selectedAlbumLabel: Int = remember { 0 }
    var selectedPhotoDetail: Int = remember { 0 }
    val locationHandler = remember {
        LocationHandler(
            context = context
        )
    }
    var imageUri: Uri by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var selectedLabel: Label? by rememberSaveable { mutableStateOf(null) }
    val takePictureLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                locationHandler.getLocation { location -> lastLocation = location }
                navController.navigate(NavigateDestination.SavePhoto.route) {
                    launchSingleTop = true
                }
            } else {
                imageUri = Uri.EMPTY
                navController.navigate(NavigateDestination.Home.route) {
                    popUpTo(NavigateDestination.Home.route) { inclusive = false }
                    selectedLabel = null
                    launchSingleTop = true
                }
            }
        }
    val takePicture = {
        // TODO: imageUri가 EMPTY가 아닐때 해당 파일 삭제
        val fileName = "${System.currentTimeMillis()}.jpg"
        val imageFile = File(context.filesDir, fileName)
        imageUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            try {
                takePictureLauncher.launch(this)
            } catch (e: ActivityNotFoundException) {
                // TODO: 카메라 전환 오류 처리
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavigateDestination.Home.route
    ) {
        composable(NavigateDestination.Home.route) {
            HomeScreen(
                locationHandler = locationHandler,
                takePicture = { takePicture() },
                onNavigateToAlbum = { navController.navigate(NavigateDestination.Album.route) },
                onNavigateToMap = { navController.navigate(NavigateDestination.Map.route) },
                onNavigateToMyPage = { navController.navigate(NavigateDestination.MyPage.route) },
            )
        }

        composable(NavigateDestination.SavePhoto.route) {
            SavePhotoScreen(
                location = lastLocation,
                model = imageUri,
                onBack = { takePicture() },
                onSave = {
                    navController.navigate(NavigateDestination.Album.route) {
                        popUpTo(NavigateDestination.Home.route) { inclusive = false }
                        selectedLabel = null
                    }
                },
                label = selectedLabel,
                onLabelSelect = {
                    navController.navigate(NavigateDestination.SearchLabel.route)
                },
                onNavigateToMyPage = { navController.navigate(NavigateDestination.MyPage.route) },
            )
        }

        composable(NavigateDestination.SearchLabel.route) {
            LabelSearchScreen { label ->
                selectedLabel = label
                navController.popBackStack()
            }
        }

        composable(NavigateDestination.Album.route) {
            AlbumScreen(
                onLabelClick = { labelId ->
                    selectedAlbumLabel = labelId
                    navController.navigate(NavigateDestination.AlbumFolder.route)
                },
                onNavigateToMyPage = { navController.navigate(NavigateDestination.MyPage.route) },
            )
        }

        composable(NavigateDestination.AlbumFolder.route) {
            AlbumFolderScreen(
                selectedAlbumLabel,
                onPhotoClick = { labelId ->
                    selectedPhotoDetail = labelId
                    navController.navigate(NavigateDestination.PhotoInfo.route)
                },
                onNavigateToMyPage = { navController.navigate(NavigateDestination.MyPage.route) },
            )
        }

        composable(NavigateDestination.PhotoInfo.route) {
            PhotoInfo(
                selectedPhotoDetail = selectedPhotoDetail,
                onNavigateToMyPage = { navController.navigate(NavigateDestination.MyPage.route) },
            )
        }

        composable(NavigateDestination.MyPage.route) {
            MyPageScreen(navigateToHome = { navController.navigate(NavigateDestination.Home.route) })
        }

        composable(NavigateDestination.Map.route) {
            MapScreen()
        }
    }
}
