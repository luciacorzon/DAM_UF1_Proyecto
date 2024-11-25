package com.example.artspace.data

import com.example.artspace.R
import com.example.artspace.model.MenuItem


class MainMenuData {
    fun LoadMenuItem() : List<MenuItem> {
        return listOf<MenuItem>(
            MenuItem(R.string.online_gallery, R.drawable.mapa_color),
            MenuItem(R.string.your_gallery, R.drawable.your_gallery)
        )
    }
}