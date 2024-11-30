package com.example.artspace.network

import com.example.artspace.model.ArtObject
import com.example.artspace.model.DetailedArtworkModel
import com.google.gson.annotations.SerializedName

data class ArtworkDetailsResponse(
    @SerializedName("elapsedMilliseconds")
    val elapsedMilliseconds: Int,

    @SerializedName("artObject")
    val artObject: DetailedArtworkModel? // Aquí está el campo artObject
)
