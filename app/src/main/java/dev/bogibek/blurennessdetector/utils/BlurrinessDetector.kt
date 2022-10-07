package dev.bogibek.blurennessdetector.utils

import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.pow

private val kernel = intArrayOf(
    0, 1, 0,
    1, -4, 1,
    0, 1, 0
)

suspend fun isBlurredPhoto(bitmap: Bitmap): Boolean {
    return withContext(Dispatchers.Default) {
        try {
            val height = bitmap.height
            val width = bitmap.width
            val convertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)

            val canvas = Canvas(convertedBitmap)
            val paint = Paint()
            paint.colorFilter = colorMatrixFilter
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            val pixelsRGB = IntArray(convertedBitmap.width * convertedBitmap.height)
            convertedBitmap.getPixels(
                pixelsRGB,
                0,
                convertedBitmap.width,
                0,
                0,
                convertedBitmap.width,
                convertedBitmap.height
            )

            val pixels = pixelsRGB.map { pixel ->
                (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)).coerceIn(0, 255)
            }.toIntArray()

            // determine edges (high frequency signals) via convolution with 3x3 LoG kernel
            // conv is the resulting flattened image, the same size as the original
            val output = IntArray(bitmap.width * bitmap.height) { 0 }

            // we iterate on every pixel of the image...
            for (y in 0 until bitmap.height) {
                for (x in 0 until bitmap.width) {
                    // ...and on every coefficient of the 3x3 kernel...
                    var convPixel = 0
                    for (j in -1 until 2) {
                        for (i in -1 until 2) {
                            // ...and we compute the dot product (the sum of an element-wise multiplication)
                            // of the kernel (sliding window) with the current region of the image it is
                            // passing through, and store the result on the corresponding pixel of the convoluted image

                            // if the image pixel required is "outside" the image, the border pixels will be
                            // replicated. otherwise, the sum of indices will point to a valid pixel
                            val pixelY = (y + j).coerceIn(0, bitmap.height - 1)
                            val pixelX = (x + i).coerceIn(0, bitmap.width - 1)
                            val pixelIndex = pixelY * bitmap.width + pixelX
                            val kernelIndex = (j + 1) * 3 + (i + 1)

                            // then, one of the products is computed and accumulated
                            convPixel += (pixels[pixelIndex] * kernel[kernelIndex])
                        }
                    }

                    // finally, the sum of the products is stored as a pixel
                    output[y * bitmap.width + x] = convPixel.coerceIn(0, 255)
                }
            }

            val mean = output.average()
            val variance =
                output.fold(0.0) { diff, pixel -> diff + (pixel - mean).pow(2) } / output.size

            variance <= 60
        } catch (t: Throwable) {
            false
        }
    }
}