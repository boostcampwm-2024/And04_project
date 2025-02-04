package com.and04.naturealbum.ui.navigation

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.and04.naturealbum.ui.add.labelsearch.navigation.labelSearchNavigation
import com.and04.naturealbum.ui.add.savephoto.navigation.saveAlbumNavGraph
import com.and04.naturealbum.ui.album.labelphotos.navigation.albumFolderNavigation
import com.and04.naturealbum.ui.album.labels.navigation.labelsAlbumNavigation
import com.and04.naturealbum.ui.album.photoinfo.navigation.photoInfoNavigation
import com.and04.naturealbum.ui.mypage.friendsearch.navigation.friendSearchNavigation
import com.and04.naturealbum.ui.home.navigation.homeNavGraph
import com.and04.naturealbum.ui.maps.navigation.mapNavigation
import com.and04.naturealbum.ui.mypage.navigation.myPageNavigation

@Composable
fun NatureAlbumNavHost(
    state: NatureAlbumState,
    navigator: NatureAlbumNavigator,
    takePictureLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
){
    NavHost(
        navController = navigator.navController,
        startDestination = NavigateDestination.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {
        homeNavGraph(
            state = state,
            navigator = navigator,
            takePictureLauncher = takePictureLauncher
        )

        saveAlbumNavGraph(
            state = state,
            navigator = navigator,
            takePictureLauncher = takePictureLauncher
        )

        labelSearchNavigation(
            state = state,
            navigator = navigator
        )

        labelsAlbumNavigation(
            navigator = navigator
        )

        albumFolderNavigation(
            navigator = navigator
        )

        photoInfoNavigation(
            navigator = navigator
        )

        myPageNavigation(
            navigator = navigator
        )

        mapNavigation(
            navigator = navigator
        )

        friendSearchNavigation(
            navigator = navigator
        )
    }
}
