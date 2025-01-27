package com.and04.naturealbum.ui.album.labelphotos.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labelphotos.AlbumFolderScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.albumFolderNavigation(
    navigator: NatureAlbumNavigator,
) {
    composable("${NavigateDestination.AlbumFolder.route}/{labelId}") { backStackEntry ->
        val labelId = backStackEntry.arguments?.getString("labelId")?.toInt()!!

        AlbumFolderScreen(
            selectedAlbumLabel = labelId,
            onPhotoClick = { photoDetailId -> navigator.navigateToAlbumInfo(photoDetailId) },
            onNavigateToMyPage = { navigator.navigateToMyPage() },
            navigateToBackScreen = { navigator.popupBackStack() },
            onNavigateToAlbum = {
                navigator.popupBackStack()
            }
        )
    }
}
