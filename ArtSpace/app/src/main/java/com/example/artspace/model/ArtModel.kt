package com.example.artspace.model

import com.google.gson.annotations.SerializedName

data class ArtModel(
    @SerializedName("objectNumber")
    var objectNumber: String,

    @SerializedName("title")
    var title: String,

    @SerializedName("principalOrFirstMaker")
    var author: String,

    @SerializedName("webImage")
    var webImage: WebImage?,

    @SerializedName("description")
    var description: String?,

    @SerializedName("materials")
    var materials: List<String>?,

    @SerializedName("techniques")
    var techniques: List<String>?,

    @SerializedName("objectTypes")
    var objectTypes: List<String>?,
)
