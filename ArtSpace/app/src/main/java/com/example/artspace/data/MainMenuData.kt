package com.example.artspace.data

import com.example.artspace.R
import com.example.artspace.model.MenuItem


class MainMenuData {
    fun LoadMenuItem() : List<MenuItem> {
        return listOf<MenuItem>(
            MenuItem(R.string.popular_category, R.drawable.vangoghportrait),
            MenuItem(R.string.discipline_category, R.drawable.scultpure),
            MenuItem(R.string.period_category, R.drawable.nightwatch),
            MenuItem(R.string.technique_category, R.drawable.rivervalley),
            MenuItem(R.string.random_category, R.drawable.surpriseartwork)
        )
    }
}