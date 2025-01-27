package com.and04.naturealbum.ui.home.navigation

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.home.HomeScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.homeNavGraph(
    state: NatureAlbumState,
    navigator: NatureAlbumNavigator,
    takePictureLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    composable(NavigateDestination.Home.route) {
        HomeScreen(
            locationHandler = state.locationHandler.value,
            takePicture = { state.takePicture(takePictureLauncher) },
            onNavigateToAlbum = { navigator.navigateToAlbum() },
            onNavigateToMap = { navigator.navigateToMap() },
            onNavigateToMyPage = { navigator.navigateToMyPage() },
        )
    }
}
