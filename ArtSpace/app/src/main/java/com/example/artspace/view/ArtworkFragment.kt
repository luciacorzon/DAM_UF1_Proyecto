package com.example.artspace.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.artspace.R
import com.example.artspace.data.javaClasses.UserDAO
import com.example.artspace.viewmodels.ArtViewModel
import com.example.artspace.databinding.FragmentArtworkBinding

class ArtworkFragment : Fragment() {

    private lateinit var binding: FragmentArtworkBinding
    private lateinit var userDAO: UserDAO

    private var isFavorite: Boolean = false

    private val artViewModel: ArtViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtworkBinding.inflate(inflater, container, false)

        userDAO = UserDAO(requireContext())

        // Recuperar usuario desde sharedPreferences
        val username = loadUserFromPreferences()

        val artId = arguments?.getString("artId")
        val cleanedArtId = artId?.let {
            if (it.startsWith("en-")) {
                it.substring(3)
            } else {
                it
            }
        }

        cleanedArtId?.let {
            artViewModel.getArtworkDetails(it)
            isFavorite = isArtworkFavorite(it)
            updateFabIcon()
        }

        artViewModel.artworkDetails.observe(viewLifecycleOwner, { artwork ->
            artwork?.let {
                val countryValue = it.primaryProductionPlace.takeIf { it != "Unknown Country" }
                    ?: getString(R.string.unknown_country)

                binding.artName.text = it.title
                binding.author.text = it.author
                binding.country.text = countryValue
                binding.year.text = it.dating?.year?.toString() ?: getString(R.string.unknown)
                binding.description.text = it.description ?: getString(R.string.not_available)

                it.webImage?.url?.let { imageUrl ->
                    Glide.with(requireContext()).load(imageUrl).into(binding.largeImage)
                }
            } ?: run {
                binding.artName.text = getString(R.string.not_available)
            }
        })

        binding.fabFavorite.setOnClickListener {
            if (artId != null) {
                if (isFavorite) {
                    removeArtworkFromFavorites(artId)
                    isFavorite = false
                } else {
                    addArtworkToFavorites(artId)
                    isFavorite = true
                }
                updateFabIcon()
            }
        }

        return binding.root
    }

    private fun updateFabIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

    private fun addArtworkToFavorites(artId: String) {
        val username = loadUserFromPreferences()
        userDAO.saveFavorite(userDAO.getUser(username), artId)
    }

    private fun removeArtworkFromFavorites(artId: String) {
        val username = loadUserFromPreferences()
        val user = userDAO.getUser(username)
        user.favorites = userDAO.returnFavoritesForUser(userDAO.favoritesFile, username)

        if (user != null && user.favorites.contains(artId)) {
            userDAO.deleteFavorite(user, artId)
            isFavorite = false

            user.favorites = userDAO.returnFavoritesForUser(userDAO.getFavoritesFile(), username)

            updateFabIcon()
        } else {
            Log.d("favoriteListener", "Obra no encontrada en favoritos: $artId")
        }
    }

    private fun isArtworkFavorite(artId: String): Boolean {
        val username = loadUserFromPreferences()
        if (username == null) {
            Log.d("Favorites", "Nombre de usuario no encontrado.")
            return false
        }

        val user = userDAO.getUser(username)
        if (user == null) {
            Log.d("Favorites", "Usuario no encontrado: $username")
            return false
        }

        user.favorites = userDAO.returnFavoritesForUser(userDAO.favoritesFile, username)
        userDAO.printUserFavorites(userDAO.favoritesFile)
        return user.favorites.contains(artId)
    }

    private fun loadUserFromPreferences(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", null)
    }
}
