package com.example.artspace.data

import com.example.artspace.R
import com.example.artspace.model.MenuItem

class SecondaryMenuData {
    fun LoadMenuItem() : List<MenuItem> {
        return listOf<MenuItem>(
            MenuItem(R.string.paint, R.drawable.perla_closeup),
            MenuItem(R.string.photography, R.drawable.red_veil),
            MenuItem(R.string.sculpture, R.drawable.veiled_virgin)
        )
    }
}