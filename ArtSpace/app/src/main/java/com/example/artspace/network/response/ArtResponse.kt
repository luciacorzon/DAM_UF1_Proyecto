package com.example.artspace.network.response

import com.example.artspace.model.ArtModel
import com.google.gson.annotations.SerializedName

data class ArtResponse(
    @SerializedName("artObjects")
    val artObjects: List<ArtModel>,
    @SerializedName("count")
    val count: Int
)
