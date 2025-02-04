package com.and04.naturealbum.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun HomeScreenLandscape(
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
        MainBackground(Modifier.fillMaxSize())
        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            //왼쪽
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // TODO:  나중에 새로운 컴포넌트 추가
                }
            }

            //오른쪽
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 4.dp)
            ) {
                Column {
                    // 나의 생물지 도
                    Box(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .align(Alignment.CenterHorizontally)
                            .background(Color.Transparent)
                    ) {
                        ClippingButtonWithFile(
                            modifier = Modifier,
                            isFromAssets = true,
                            fileNameOrResId = MAP_BUTTON_BACKGROUND_OUTLINE_SVG,
                            text = stringResource(R.string.home_navigate_to_map),
                            textColor = Color.Black,
                            imageResId = R.drawable.btn_home_menu_map_background,
                            onClick = onNavigateToMap
                        )
                    }

                    // 나의도감, 카메라
                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                        NavigateContent(
                            modifier = Modifier.fillMaxWidth(),
                            onClickCamera = onClickCamera,
                            onNavigateToAlbum = onNavigateToAlbum
                        )

                    }
                }
            }
        }
    }
}
