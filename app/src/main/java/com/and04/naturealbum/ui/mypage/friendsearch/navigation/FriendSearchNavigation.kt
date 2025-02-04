package com.and04.naturealbum.ui.mypage.friendsearch.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.mypage.friendsearch.FriendSearchScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.friendSearchNavigation(
    navigator: NatureAlbumNavigator
) {
    composable(NavigateDestination.FriendSearch.route) { backStackEntry ->
        val backStackEntryForMyPage = remember(backStackEntry) {
            navigator.getNavBackStackEntry(NavigateDestination.MyPage.route)
        }
        FriendSearchScreen(
            onBack = { navigator.popupBackStack() },
            friendViewModel = hiltViewModel(backStackEntryForMyPage),
            networkViewModel = hiltViewModel(backStackEntryForMyPage),
        )
    }
}
