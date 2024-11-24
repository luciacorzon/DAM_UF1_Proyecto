package com.example.artspace.data

import com.example.artspace.R
import com.example.artspace.model.ArtworkItem

class ArtData {
    fun LoadArtItem() : List<ArtworkItem> {
        return listOf<ArtworkItem>(
            ArtworkItem(R.drawable.perla, R.string.art_name, R.string.author),
            ArtworkItem(R.drawable.venus, R.string.art_name, R.string.author),
            ArtworkItem(R.drawable.perla, R.string.art_name, R.string.author),
            ArtworkItem(R.drawable.venus, R.string.art_name, R.string.author),
            ArtworkItem(R.drawable.perla, R.string.art_name, R.string.author),
            ArtworkItem(R.drawable.venus, R.string.art_name, R.string.author),
            ArtworkItem(R.drawable.perla, R.string.art_name, R.string.author)
        )
    }
}