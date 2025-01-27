package com.and04.naturealbum.ui.add.labelsearch

import androidx.compose.ui.graphics.luminance
import com.and04.naturealbum.utils.color.toColor

fun getRandomColor(): String {
    while (true) {
        val r = (0..255).random()
        val g = (0..255).random()
        val b = (0..255).random()

        val color = "FF${r.toHex()}${g.toHex()}${b.toHex()}".toColor()
        val luminance = color.luminance()

        if (luminance < 0.9f && luminance > 0.1f) {
            return "FF${r.toHex()}${g.toHex()}${b.toHex()}"
        }
    }
}

private fun Int.toHex(): String {
    val hex = Integer.toHexString(this)
    return if (hex.length == 1) "0$hex" else hex
}
