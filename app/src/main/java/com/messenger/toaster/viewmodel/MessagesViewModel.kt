package com.messenger.toaster.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.messenger.toaster.dto.FileDTO
import kotlinx.coroutines.flow.MutableStateFlow

class MessagesViewModel:ViewModel() {
    private val _images = MutableStateFlow<List<FileDTO>>(emptyList())
    val images = _images

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded

    fun uploadImages(images:List<Uri>){

    }

    private fun upload(images: List<Uri>){

    }
}