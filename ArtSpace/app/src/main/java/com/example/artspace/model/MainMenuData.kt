package com.example.artspace.model

import com.example.artspace.R


class MainMenuData {
    fun LoadMenuItem() : List<MenuItem> {
        return listOf<MenuItem>(
            MenuItem(R.string.online_gallery, R.drawable.tu_galeria),
            MenuItem(R.string.your_gallery, R.drawable.tu_galeria)
        )
    }
}