package com.and04.naturealbum.ui.album.labelphotos

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R

@Composable
fun ButtonWithAnimation(
    selectAll: (Boolean) -> Unit,
    savePhotos: () -> Unit,
    deletePhotos: () -> Unit,
    editMode: State<Boolean>,
    modifier: Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val transitionOffset by animateDpAsState(
        targetValue = if (editMode.value) 0.dp else -screenWidth,
        animationSpec = tween(durationMillis = 500), label = "button_animation"
    )

    Box(
        modifier = modifier
    ) {
        // 편집 모드 -> 검은색 박스와 버튼들
        Box(
            modifier = Modifier
                .offset(x = transitionOffset)
                .fillMaxWidth()
                .alpha(if (editMode.value) 1f else 0f) // editMode가 활성화 시 보이게 함
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // 버튼들을 가로 중앙에 배치
            ) {
                ToggleButton(selectAll)
                Button(
                    onClick = { savePhotos() }
                ) {
                    Text(stringResource(R.string.album_folder_screen_save_button))
                }
                Button(
                    onClick = { deletePhotos() }
                ) {
                    Text(stringResource(R.string.album_folder_screen_delete_button))
                }
            }
        }

        // 일반 모드 -> 빨간색 박스와 라벨
        Box(
            modifier = Modifier
                .offset(x = if (editMode.value) screenWidth else 0.dp)
                .fillMaxWidth()
                .background(Color.Black)
                .alpha(if (!editMode.value) 1f else 0f)
        ) {

        }
    }
}

@Composable
fun ToggleButton(
    selectAll: (Boolean) -> Unit,
) {
    var isToggled by remember { mutableStateOf(false) } // 토글 상태

    OutlinedButton(
        onClick = {
            isToggled = !isToggled
            selectAll(isToggled)
        }, // 클릭 시 토글 상태 변경
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isToggled) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isToggled) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // 테두리는 primary 색상 유지
    ) {
        Text(stringResource(R.string.album_folder_screen_select_all))
    }
}
