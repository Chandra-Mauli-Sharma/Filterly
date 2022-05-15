package com.example.filterly.api

import com.example.filterly.model.FilterImageProp
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FilterApi {
    @GET("/api/image_filter/get/{fileLoc}")
    suspend fun getImage(@Path("fileLoc") fileLoc: String): Response<String>

    @Multipart
    @POST("api/image_filter/add/{effect}/{filename}")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Path("effect") effect: String,
        @Path("filename") filename: String
    ): Response<String>
}