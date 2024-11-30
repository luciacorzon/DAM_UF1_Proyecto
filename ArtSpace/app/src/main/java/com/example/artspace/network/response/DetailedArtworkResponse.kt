package com.example.artspace.network.response

import com.example.artspace.model.DetailedArtworkModel
import com.google.gson.annotations.SerializedName

data class ArtworkResponse(
    @SerializedName("elapsedMilliseconds")
    var elapsedMilliseconds: Int,

    @SerializedName("artObject")
    var artObject: DetailedArtworkModel
)

