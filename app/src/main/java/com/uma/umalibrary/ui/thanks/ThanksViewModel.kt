package com.uma.umalibrary.ui.thanks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThanksViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is thanks Fragment"
    }
    val text: LiveData<String> = _text
}