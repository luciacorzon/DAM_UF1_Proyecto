package com.example.artspace.model

import com.google.gson.annotations.SerializedName

data class DetailedDating(
    @SerializedName("sortingDate")
    var year: Int?,
    @SerializedName("presentingDate")
    var presentingDate: String?
)
