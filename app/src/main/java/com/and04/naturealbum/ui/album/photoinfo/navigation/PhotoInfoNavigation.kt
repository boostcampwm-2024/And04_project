package com.and04.naturealbum.ui.album.photoinfo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.photoinfo.PhotoInfo
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.photoInfoNavigation(
    navigator: NatureAlbumNavigator
) {
    composable("${NavigateDestination.PhotoInfo.route}/{photoDetailId}") { backStackEntry ->
        val photoDetailId = backStackEntry.arguments?.getString("photoDetailId")?.toInt()!!

        PhotoInfo(
            selectedPhotoDetail = photoDetailId,
            onNavigateToMyPage = { navigator.navigateToMyPage() },
            navigateToBackScreen = { navigator.popupBackStack() },
        )
    }
}
