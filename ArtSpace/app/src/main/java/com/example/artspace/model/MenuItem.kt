package com.example.artspace.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItem(
    @StringRes val nameRes: Int,
    @DrawableRes val imageRes: Int
)
