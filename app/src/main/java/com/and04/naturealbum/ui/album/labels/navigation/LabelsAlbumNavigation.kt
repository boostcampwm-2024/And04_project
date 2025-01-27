package com.and04.naturealbum.ui.album.labels.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labels.AlbumScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.labelsAlbumNavigation(
    navigator: NatureAlbumNavigator
) {
    composable(NavigateDestination.Album.route) {
        AlbumScreen(
            onLabelClick = { labelId -> navigator.navigateToAlbumFolder(labelId) },
            onNavigateToMyPage = { navigator.navigateToMyPage() },
            navigateToBackScreen = { navigator.popupBackStack() },
        )
    }
}
