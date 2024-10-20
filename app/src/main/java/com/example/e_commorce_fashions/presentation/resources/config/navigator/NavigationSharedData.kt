package com.example.e_commorce_fashions.presentation.resources.config.navigator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedData : ViewModel() {

    private val _data = MutableLiveData<MutableMap<String, Any?>>().apply { value = mutableMapOf() }

    fun clearData(screenKey: String) {
        _data.value?.remove(screenKey)
//        _data.postValue(_data.value) // Notify observers of the change
    }

    fun <T> setData(screenKey: String, newData: T?) {
        _data.value?.put(screenKey, newData)
//        _data.postValue(_data.value) // Notify observers of the change
    }

    fun <T> getData(screenKey: String): T? {
        return _data.value?.get(screenKey) as T?
    }
}