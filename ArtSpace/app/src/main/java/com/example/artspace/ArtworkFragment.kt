package com.example.artspace

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.artspace.viewmodels.ArtViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ArtworkFragment : Fragment() {

    private lateinit var fabFavorite: FloatingActionButton
    private var isFavorite: Boolean = false

    private lateinit var artName: TextView
    private lateinit var author: TextView
    private lateinit var country: TextView
    private lateinit var year: TextView
    private lateinit var description: TextView
    private lateinit var imageView: ImageView

    private val artViewModel: ArtViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artwork, container, false)

        fabFavorite = view.findViewById(R.id.fab_favorite)
        artName = view.findViewById(R.id.art_name)
        author = view.findViewById(R.id.author)
        country = view.findViewById(R.id.country)
        year = view.findViewById(R.id.year)
        description = view.findViewById(R.id.description)
        imageView = view.findViewById(R.id.largeImage)

        val artId = arguments?.getString("artId")
        artId?.let {
            artViewModel.getArtworkDetails(it)
        }

        val unknown = getString(R.string.unknown)
        val available = getString(R.string.not_available)

        artViewModel.artworkDetails.observe(viewLifecycleOwner, { artwork ->
            artwork?.let {
                val countryValue = it.primaryProductionPlace.takeIf { it != "Unknown Country" }
                    ?: getString(R.string.unknown_country)

                artName.text = it.title
                author.text = it.author
                country.text = countryValue
                year.text = it.dating?.year?.toString() ?: getString(R.string.unknown)
                description.text = it.description ?: getString(R.string.not_available)

                it.webImage?.url?.let { imageUrl ->
                    Glide.with(requireContext()).load(imageUrl).into(imageView)
                }
            } ?: run {
                artName.text = getString(R.string.not_available)
            }
        })


        isFavorite = loadFavoriteState()
        updateFabIcon()
        fabFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFabIcon()
            saveFavoriteState(isFavorite)
        }

        return view
    }

    private fun updateFabIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            fabFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

    private fun saveFavoriteState(isFavorite: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFavorite", isFavorite)
        editor.apply()
    }

    private fun loadFavoriteState(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFavorite", false)
    }
}
