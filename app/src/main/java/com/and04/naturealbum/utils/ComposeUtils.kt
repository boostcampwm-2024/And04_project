package com.and04.naturealbum.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.component.NatureAlbumLandscapeTopAppBar
import com.and04.naturealbum.ui.component.NatureAlbumPortraitTopAppBar

const val COLUMN_COUNT_PORTRAIT = 2
const val COLUMN_COUNT_LANDSCAPE = 4

fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun Context.gridColumnCount(): Int {
    return if (isPortrait()) COLUMN_COUNT_PORTRAIT else COLUMN_COUNT_LANDSCAPE
}

@Composable
fun Context.GetTopBar(
    title: String = stringResource(R.string.app_name),
    type: AppBarType,
    navigateToMyPage: () -> Unit = {},
    navigateToBackScreen: () -> Unit = {},
) {
    if (isPortrait()) {
        NatureAlbumPortraitTopAppBar(
            title = title,
            type = type,
            navigateToMyPage = navigateToMyPage,
            navigateToBackScreen = navigateToBackScreen
        )
    } else {
        NatureAlbumLandscapeTopAppBar(
            title = title,
            type = type,
            navigateToMyPage = navigateToMyPage,
            navigateToBackScreen = navigateToBackScreen,
        )
    }
}
