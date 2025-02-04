package com.and04.naturealbum.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.utils.image.parseDrawableSvgFile
import com.and04.naturealbum.utils.image.parseSvgFile

private fun Path.scale(scaleX: Float, scaleY: Float) {
    val matrix = Matrix().apply {
        reset()
        scale(scaleX, scaleY)
    }
    transform(matrix)
}

private class SvgOutlineShape(
    private val path: Path,
    private val viewportWidth: Float,
    private val viewportHeight: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {

        val scaleX = size.width / viewportWidth
        val scaleY = size.height / viewportHeight
        val scale = minOf(scaleX, scaleY)


        val scaledPath = Path().apply {
            addPath(path)
            scale(scale, scale)
        }
        return Outline.Generic(scaledPath)
    }
}

@Composable
fun ClippingButtonWithFile(
    modifier: Modifier,
    isFromAssets: Boolean = true,
    fileNameOrResId: Any,
    text: String,
    textColor: Color,
    imageResId: Int,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val svgData = when {
        isFromAssets && fileNameOrResId is String -> parseSvgFile(context, fileNameOrResId)
        !isFromAssets && fileNameOrResId is Int -> parseDrawableSvgFile(
            context,
            fileNameOrResId
        )

        else -> null
    }

    val parsedPathData = svgData?.pathData ?: ""
    val parsedViewportWidth = svgData?.viewportWidth ?: 1f
    val parsedViewportHeight = svgData?.viewportHeight ?: 1f

    ClippingButton(
        modifier = modifier,
        text = text,
        textColor = textColor,
        imageResId = imageResId,
        pathData = parsedPathData,
        viewportWidth = parsedViewportWidth,
        viewportHeight = parsedViewportHeight,
        onClick = onClick
    )
}

@Composable
private fun ClippingButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    imageResId: Int,
    pathData: String,
    viewportWidth: Float,
    viewportHeight: Float,
    onClick: () -> Unit,
) {

    val path = PathParser().parsePathString(pathData).toPath()

    Box(
        modifier = modifier
            .aspectRatio(viewportWidth / viewportHeight)
            .clip(SvgOutlineShape(path, viewportWidth, viewportHeight))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = stringResource(R.string.cliping_button_navigate_to_map),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp, top = 36.dp),
            color = textColor
        )
    }
}


@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    name = "ClippingButtonWithFile Preview (Dark Mode)"
)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO,
    name = "ClippingButtonWithFile Preview (Light Mode)"
)
@Composable
private fun ClippingButtonWithFilePreview() {
    val context = LocalContext.current
    ClippingButtonWithFile(
        modifier = Modifier,
        isFromAssets = true,
        fileNameOrResId = "btn_home_menu_map_background_outline.svg",
        text = stringResource(R.string.home_navigate_to_map),
        textColor = Color.Black,
        imageResId = R.drawable.btn_home_menu_map_background,
        onClick = { /* TODO */ }
    )
}

