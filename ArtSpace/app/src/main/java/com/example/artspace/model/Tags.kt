package com.example.artspace.model

import com.google.gson.annotations.SerializedName

data class Tags(
    val name: String?,
    val tourism: String?,
    @SerializedName("addr:city")
    val city: String?
)

