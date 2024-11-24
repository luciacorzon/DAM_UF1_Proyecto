package com.example.artspace.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ArtworkItem (
    @DrawableRes val artImageRes: Int,
    @StringRes val artNameRes: Int,
    @StringRes val artAuthorRes: Int
)