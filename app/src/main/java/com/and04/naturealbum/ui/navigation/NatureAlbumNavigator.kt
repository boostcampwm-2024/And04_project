package com.and04.naturealbum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NatureAlbumNavigator(
    val navController: NavHostController,
) {
    fun popupBackStack() {
        navController.popBackStack()
    }

    fun getNavBackStackEntry(route: String) = navController.getBackStackEntry(route)

    fun navigateToAlbum() =
        navController.navigate(NavigateDestination.Album.route)

    fun navigateToMap() = navController.navigate(NavigateDestination.Map.route)

    fun navigateToMyPage() = navController.navigate(NavigateDestination.MyPage.route) {
        launchSingleTop = true
    }

    fun navigateToSearchLabel() = navController.navigate(NavigateDestination.SearchLabel.route)

    fun navigateToHome() = navController.navigate(NavigateDestination.Home.route)

    fun navigateCameraToHome() = navController.navigate(NavigateDestination.Home.route) {
        popUpTo(NavigateDestination.Home.route) { inclusive = false }
        launchSingleTop = true
    }

    fun navigateHomeToSaveAlbum() =
        navController.navigate(NavigateDestination.SavePhoto.route) {
            launchSingleTop = true
        }

    fun navigateSavePhotoToAlbum() {
        navController.navigate(NavigateDestination.Album.route) {
            popUpTo(NavigateDestination.Home.route) { inclusive = false }
        }
    }

    fun navigateToAlbumFolder(labelId: Int) {
        navController.navigate("${NavigateDestination.AlbumFolder.route}/$labelId")
    }

    fun navigateToAlbumInfo(photoDetailId: Int) {
        navController.navigate("${NavigateDestination.PhotoInfo.route}/$photoDetailId")
    }

    fun navigateToFriendSearch() = navController.navigate(NavigateDestination.FriendSearch.route)
}

@Composable
fun rememberNatureAlbumNavigator(
    navController: NavHostController = rememberNavController(),
): NatureAlbumNavigator = remember(navController) {
    NatureAlbumNavigator(navController)
}
