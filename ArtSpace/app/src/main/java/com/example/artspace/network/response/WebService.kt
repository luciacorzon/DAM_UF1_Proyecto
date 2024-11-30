package com.example.artspace.network.response

import com.example.artspace.network.ArtResponse
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    @GET("collection")
    suspend fun getArtworks(
        @Query("key") apiKey: String,
        @Query("type") type: String, // painting, sculpture, etc.
        @Query("ps") pageSize: Int = 10 // Número de resultados por página
    ): Response<ArtResponse>
}