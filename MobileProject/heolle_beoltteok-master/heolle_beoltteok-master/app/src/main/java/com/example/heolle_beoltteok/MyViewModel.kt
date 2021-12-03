package com.example.heolle_beoltteok

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val liveData = MutableLiveData<String>()

    fun getValue() :String {
        return liveData.value!!
    }
}