package com.example.artspace.model

import com.google.gson.annotations.SerializedName

data class DetailedArtworkModel(
    @SerializedName("id")
    var id: String,

    @SerializedName("title")
    var title: String,

    @SerializedName("principalOrFirstMaker")
    var author: String,

    @SerializedName("productionPlaces")
    var productionPlaces: List<String>?,

    @SerializedName("dating")
    var dating: DetailedDating?,

    @SerializedName("plaqueDescriptionEnglish")
    var description: String?,

    @SerializedName("webImage")
    var webImage: WebImage?
) {

    val primaryProductionPlace: String
        get() = productionPlaces?.firstOrNull() ?: "Unknown Country"
}
