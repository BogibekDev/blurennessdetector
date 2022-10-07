package dev.bogibek.blurennessdetector.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.bogibek.blurennessdetector.viewmodel.BlurrinessViewModel

class BlurrinessFactory(private val repository: BlurrinnessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlurrinessViewModel::class.java)) {
            return BlurrinessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}