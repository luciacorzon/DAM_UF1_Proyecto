package com.example.artspace.network

import com.example.artspace.network.response.OverpassResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApiClient {
    @GET("api/interpreter")
    fun getMuseumsNearby(
        @Query("data") query: String
    ): Call<OverpassResponse>

    @GET("api/interpreter")
    fun getCulturalSitesNearby(
        @Query("data") query: String
    ): Call<OverpassResponse>
}