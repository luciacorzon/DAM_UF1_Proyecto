package com.example.artspace.model

import com.google.gson.annotations.SerializedName

data class ArtModel(
    @SerializedName("objectNumber")  // Cambié el nombre aquí
    var objectNumber: String,


    @SerializedName("title")
    var title: String,

    @SerializedName("principalOrFirstMaker")
    var author: String,

    @SerializedName("webImage")
    var webImage: WebImage?,

    @SerializedName("description")  // Descripción de la obra
    var description: String?,

    @SerializedName("materials")  // Materiales utilizados en la obra
    var materials: List<String>?,

    @SerializedName("techniques")  // Técnicas utilizadas en la obra
    var techniques: List<String>?,

    @SerializedName("objectTypes")  // Tipos de objeto (e.g. photograph, sculpture)
    var objectTypes: List<String>?,
)
