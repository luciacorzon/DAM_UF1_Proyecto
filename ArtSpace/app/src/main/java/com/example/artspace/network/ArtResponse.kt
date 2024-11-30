package com.example.artspace.network

import com.example.artspace.model.ArtModel
import com.google.gson.annotations.SerializedName

data class ArtResponse(
    @SerializedName("artObjects")
    val artObjects: List<ArtModel>, // Lista de objetos de arte

    @SerializedName("count")
    val count: Int // Número total de objetos de arte disponibles
)
