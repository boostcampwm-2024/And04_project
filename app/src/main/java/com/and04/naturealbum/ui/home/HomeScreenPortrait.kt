package com.and04.naturealbum.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.component.ClippingButtonWithFile
import com.and04.naturealbum.utils.GetTopBar

@Composable
fun HomeScreenPortrait(
    onClickCamera: () -> Unit,
    onNavigateToAlbum: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToMap: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            context.GetTopBar(
                type = AppBarType.Action,
                navigateToMyPage = { onNavigateToMyPage() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            MainBackground(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            ClippingButtonWithFile(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                isFromAssets = true,
                fileNameOrResId = MAP_BUTTON_BACKGROUND_OUTLINE_SVG,
                text = stringResource(R.string.home_navigate_to_map),
                textColor = Color.Black,
                imageResId = R.drawable.btn_home_menu_map_background,
                onClick = onNavigateToMap
            )

            Spacer(modifier = Modifier.height(16.dp))

            NavigateContent(
                modifier = Modifier
                    .weight(1.17f)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClickCamera = onClickCamera,
                onNavigateToAlbum = onNavigateToAlbum
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
