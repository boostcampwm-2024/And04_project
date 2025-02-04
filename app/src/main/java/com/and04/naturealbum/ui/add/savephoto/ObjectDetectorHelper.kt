package com.and04.naturealbum.ui.add.savephoto

import android.content.Context

class ObjectDetectorHelper(context: Context, modelPath: String) {
//    private val detector: ObjectDetector
//
//    init {
//        val modelFile =
//            copyAssetToInternalStorage(context, "EfficientDet.tflite")
//        val options = ObjectDetector.ObjectDetectorOptions.builder()
//            .setMaxResults(RESULT_COUNT) // 최대 5개의 결과만 반환
//            .setScoreThreshold(SCORE_THRES_HOLD) // 50% 이상의 신뢰도만 반환
//            .build()
//        detector = ObjectDetector.createFromFileAndOptions(modelFile, options)
//    }
//
//    fun detectObjects(bitmap: Bitmap): List<Detection> {
//        val tensorImage = TensorImage.fromBitmap(bitmap)
//        return detector.detect(tensorImage)
//    }
//
//    private fun copyAssetToInternalStorage(
//        context: Context,
//        assetFileName: String
//    ): File {
//        val outputFile = File(context.filesDir, assetFileName)
//        if (!outputFile.exists()) {
//            context.assets.open(assetFileName).use { inputStream ->
//                outputFile.outputStream().use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//        }
//        return outputFile
//    }
//
//    companion object {
//        private const val RESULT_COUNT = 5
//        private const val SCORE_THRES_HOLD = 0.9f
//    }
}

