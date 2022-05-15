package com.example.filterly.api

import com.example.filterly.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {


    private val retrofit by lazy {
        val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
        Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    val filterApi: FilterApi by lazy {
        retrofit.create(FilterApi::class.java)
    }
}