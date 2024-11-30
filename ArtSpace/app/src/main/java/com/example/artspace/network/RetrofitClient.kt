package com.example.artspace.network

import com.example.artspace.core.Constants
import com.example.artspace.network.response.WebService
import com.google.gson.Gson
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object RetrofitClient {
    val webService: WebService by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.API_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build().create(WebService::class.java)
    }
}