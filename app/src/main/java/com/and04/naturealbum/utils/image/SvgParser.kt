package com.and04.naturealbum.utils.image

import android.content.Context
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

private const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"

data class SvgData(
    val width: Float,
    val height: Float,
    val viewportWidth: Float,
    val viewportHeight: Float,
    val pathData: String
)

fun parseSvgFile(context: Context, fileName: String): SvgData? {
    try {
        val inputStream = context.assets.open(fileName)
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var width = 0f
        var height = 0f
        var viewportWidth = 0f
        var viewportHeight = 0f
        var pathData = ""

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "svg" -> {
                        width = parser.getAttributeValue(null, "width")?.toFloatOrNull() ?: 0f
                        height = parser.getAttributeValue(null, "height")?.toFloatOrNull() ?: 0f
                        viewportWidth = parser.getAttributeValue(null, "viewBox")?.split(" ")?.get(2)?.toFloatOrNull() ?: 0f
                        viewportHeight = parser.getAttributeValue(null, "viewBox")?.split(" ")?.get(3)?.toFloatOrNull() ?: 0f
                    }
                    "path" -> {
                        pathData = parser.getAttributeValue(null, "d") ?: ""
                    }
                }
            }
        }
        inputStream.close()
        return SvgData(width, height, viewportWidth, viewportHeight, pathData)
    } catch (e: Exception) {
        return null
    }
}

fun parseDrawableSvgFile(context: Context, resId: Int): SvgData? {
    try {
        val parser = context.resources.getXml(resId)

        var width = 0f
        var height = 0f
        var viewportWidth = 0f
        var viewportHeight = 0f
        var pathData = ""

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "vector" -> {
                        parser.getAttributeValue(ANDROID_NAMESPACE, "width")?.let { rawWidth ->
                            width = rawWidth.replace("dp", "").replace("dip", "").toFloatOrNull() ?: 0f
                        }
                        parser.getAttributeValue(ANDROID_NAMESPACE, "height")?.let { rawHeight ->
                            height = rawHeight.replace("dp", "").replace("dip", "").toFloatOrNull() ?: 0f
                        }
                        viewportWidth = parser.getAttributeValue(ANDROID_NAMESPACE, "viewportWidth")?.toFloatOrNull() ?: 0f
                        viewportHeight = parser.getAttributeValue(ANDROID_NAMESPACE, "viewportHeight")?.toFloatOrNull() ?: 0f
                    }
                    "path" -> {
                        pathData = parser.getAttributeValue(ANDROID_NAMESPACE, "pathData") ?: ""
                    }
                }
            }
        }
        return SvgData(width, height, viewportWidth, viewportHeight, pathData)
    } catch (e: Exception) {
        return null
    }
}
