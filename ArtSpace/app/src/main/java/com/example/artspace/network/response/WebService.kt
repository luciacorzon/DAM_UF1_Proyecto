package com.example.artspace.network.response

import com.example.artspace.network.ArtworkDetailsResponse
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    @GET("collection")
    suspend fun getArtworks(
        @Query("key") apiKey: String,
        @Query("type") type: String,
        @Query("ps") pageSize: Int = 10
    ): Response<ArtResponse>

    @GET("collection/{artId}")
    suspend fun getArtworkDetails(
        @Path("artId") artId: String,
        @Query("key") apiKey: String
    ): Response<ArtworkDetailsResponse>
}