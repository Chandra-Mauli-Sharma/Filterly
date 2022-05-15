package com.example.filterly.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filterly.R
import com.example.filterly.model.FilterImageProp
import com.example.filterly.repository.ApiRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class FilterViewModel(private val repository: ApiRepository) : ViewModel() {

    val filterImage: MutableLiveData<Response<String>> = MutableLiveData()
    val uploadfilterImage: MutableLiveData<Response<String>> = MutableLiveData()
    var effect:MutableLiveData<String> =MutableLiveData()


    fun getImage(fileLoc: String) {
        viewModelScope.launch {
            val response = repository.getImage(fileLoc)
            filterImage.value = response
        }
    }

    fun uploadImage(body: MultipartBody.Part, effect: String, filename: String) {
        viewModelScope.launch {
            val response = repository.uploadImage(body, effect, filename)
            uploadfilterImage.value = response
        }
    }

}