package dev.bogibek.blurennessdetector.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bogibek.blurennessdetector.repository.BlurrinnessRepository
import dev.bogibek.blurennessdetector.utils.UiStateObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BlurrinessViewModel(private val repository: BlurrinnessRepository) : ViewModel() {
    private val _blurredState = MutableStateFlow<UiStateObject<Boolean>>(UiStateObject.EMPTY)
    val blurred = _blurredState


    fun isBlurred(bitmap: Bitmap) = viewModelScope.launch {
        _blurredState.value = UiStateObject.LOADING
        try {
            val response = repository.isBlurred(bitmap)
            _blurredState.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _blurredState.value = UiStateObject.ERROR(e.localizedMessage ?: "No connection")
        }
    }
}