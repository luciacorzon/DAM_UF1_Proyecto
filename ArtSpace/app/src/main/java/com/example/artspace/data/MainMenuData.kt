package com.example.artspace.data

import com.example.artspace.R
import com.example.artspace.model.MenuItem


class MainMenuData {
    fun LoadMenuItem() : List<MenuItem> {
        return listOf<MenuItem>(
            MenuItem(R.string.online_gallery, R.drawable.gallery_online),
            MenuItem(R.string.your_gallery, R.drawable.your_gallery)
        )
    }
}