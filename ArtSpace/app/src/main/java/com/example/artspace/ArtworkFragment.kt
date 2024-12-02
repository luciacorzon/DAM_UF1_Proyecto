package com.example.artspace

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.artspace.data.javaClasses.UserDAO
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

    private lateinit var userDAO: UserDAO

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

        // Instancia el UserDAO
        userDAO = UserDAO(requireContext())

        // Recupera el nombre de usuario desde SharedPreferences
        val username = loadUserFromPreferences()
        Log.d("User", "Usuario actual: $username")

        val artId = arguments?.getString("artId")
        artId?.let {
            artViewModel.getArtworkDetails(it)
            // Verifica si esta obra está en los favoritos del usuario
            isFavorite = isArtworkFavorite(it)
            updateFabIcon()
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

        fabFavorite.setOnClickListener {
            if (artId != null) {
                // Si ya está en favoritos, lo eliminamos; si no, lo agregamos
                if (isFavorite) {
                    removeArtworkFromFavorites(artId)
                    isFavorite = false
                    Log.d("Favorites", "Obra eliminada de favoritos: $artId")
                } else {
                    addArtworkToFavorites(artId)
                    isFavorite = true
                    Log.d("Favorites", "Obra añadida a favoritos: $artId")
                }
                updateFabIcon()
            }
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

    private fun addArtworkToFavorites(artId: String) {
        val username = loadUserFromPreferences()
        userDAO.saveFavorite(userDAO.getUser(username), artId)
        Log.d("Favorites", "Favoritos actualizados. Usuario: $username, Obra: $artId")
    }

    private fun removeArtworkFromFavorites(artId: String) {
        val username = loadUserFromPreferences()
       userDAO.deleteFavorite(userDAO.getUser(username), artId)
        Log.d("Favorites", "Favoritos actualizados. Usuario: $username, Obra eliminada: $artId")
    }

    private fun isArtworkFavorite(artId: String): Boolean {
        val username = loadUserFromPreferences()
        val user = userDAO.getUser(username)

        // Asegúrate de que el usuario no sea null
        if (user == null) {
            Log.d("Favorites", "Usuario no encontrado: $username")
            return false
        }

        var fichero = userDAO.favoritesFile.name
        userDAO.loadFavorites(user, userDAO.favoritesFile.name)
        val isFavorite = user.favorites.contains(artId)
        Log.d("Favorites", "Obra $artId en favoritos: $isFavorite y fichero: $fichero")
        return isFavorite
    }


    private fun loadUserFromPreferences(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        Log.d("UserPrefs", "Usuario cargado: $username")
        return username
    }
}
