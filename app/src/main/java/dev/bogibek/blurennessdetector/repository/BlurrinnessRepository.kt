package dev.bogibek.blurennessdetector.repository

import android.graphics.Bitmap
import dev.bogibek.blurennessdetector.utils.isBlurredPhoto

class BlurrinnessRepository() {
    suspend fun isBlurred(bitmap: Bitmap) = isBlurredPhoto(bitmap)
}