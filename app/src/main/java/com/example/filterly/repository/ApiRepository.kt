package com.example.filterly.repository

import com.example.filterly.api.RetrofitInstance
import com.example.filterly.model.FilterImageProp
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class ApiRepository {

    suspend fun getImage(fileLoc:String):Response<String>{
        return RetrofitInstance.filterApi.getImage(fileLoc)
    }

    suspend fun uploadImage(body: MultipartBody.Part,effect:String,filename:String):Response<String> {
        return RetrofitInstance.filterApi.uploadImage(body,effect,filename)
    }
}