package com.example.artspace.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OverPassRetrofitClient {
    private const val BASE_URL = "https://overpass-api.de/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: OverpassApiClient = retrofit.create(OverpassApiClient::class.java)
}