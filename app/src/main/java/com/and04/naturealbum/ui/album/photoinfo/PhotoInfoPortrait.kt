package com.and04.naturealbum.ui.album.photoinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.and04.naturealbum.R
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.utils.color.toColor
import com.and04.naturealbum.utils.time.toDate

@Composable
fun PhotoInfoPortrait(
    innerPadding: PaddingValues,
    photoDetail: PhotoDetail,
    label: Label,
    address: State<String>,
    setAlbumThumbnail: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlbumLabel(
            modifier = Modifier
                .background(
                    color = label.backgroundColor.toColor(),
                    shape = CircleShape
                )
                .fillMaxWidth(0.4f),
            text = label.name,
            backgroundColor = label.backgroundColor.toColor()
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoDetail.photoUri)
                .crossfade(true)
                .build(),
            contentDescription = photoDetail.description,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(10.dp))
        )

        RowInfo(
            imgVector = Icons.Default.DateRange,
            contentDescription = stringResource(R.string.photo_info_screen_calender_icon),
            text = photoDetail.datetime.toDate()
        )

        RowInfo(
            imgVector = Icons.Default.LocationOn,
            contentDescription = stringResource(R.string.photo_info_screen_location_icon),
            text = address.value
        )

        if (photoDetail.description.isNotEmpty()) {
            RowInfo(
                imgVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.photo_info_screen_description_icon),
                text = photoDetail.description
            )
        }

        SetThumbnailContent(
            photoDetail = photoDetail,
            setAlbumThumbnail = setAlbumThumbnail,
        )
    }
}
