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
        Log.d("SharedPreferences Artwork", "Usuario actual: $username")

        val artId = arguments?.getString("artId")
        artId?.let {
            artViewModel.getArtworkDetails(it)
            // Verifica si esta obra está en los favoritos del usuario
            isFavorite = isArtworkFavorite(it)
            Log.d("ISFAOVIRTE", "$isFavorite")
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
                if (isFavorite) {
                    removeArtworkFromFavorites(artId)
                    isFavorite = false
                    Log.d("favoriteListener", "Obra eliminada de favoritos: $artId")
                } else {
                    addArtworkToFavorites(artId)
                    isFavorite = true
                    Log.d("favoriteListener", "Obra añadida a favoritos: $artId")
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
        Log.d("addFavorite", "Favoritos actualizados. Usuario: $username, Obra: $artId")
    }

    // Actualización de la eliminación de favorito:
    private fun removeArtworkFromFavorites(artId: String) {
        val username = loadUserFromPreferences()
        val user = userDAO.getUser(username)
        user.favorites = userDAO.returnFavoritesForUser(userDAO.favoritesFile, username)


        if (user != null && user.favorites.contains(artId)) {
            userDAO.deleteFavorite(user, artId)  // Eliminar la obra de los favoritos
            isFavorite = false  // Actualizar el estado de favorito
            Log.d("favoriteListener", "Obra eliminada de favoritos: $artId")

            // Recargar favoritos después de la eliminación
            user.favorites = userDAO.returnFavoritesForUser(userDAO.getFavoritesFile(), username)
            Log.d("favoriteListener", "Favoritos después de eliminar: ${user.favorites}")

            updateFabIcon()  // Actualizar el icono del FAB
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

        // Carga los favoritos del usuario desde el archivo
        user.favorites = userDAO.returnFavoritesForUser(userDAO.favoritesFile, username)
        Log.d("isArtworkFavorite", "Favoritos después de cargar: ${user.favorites}")
        userDAO.printUserFavorites(userDAO.favoritesFile)
        // No es necesario volver a cargar los favoritos desde el archivo
        val isFavorite = user.favorites.contains(artId)
        Log.d("Favorites", "Obra $artId en favoritos: $isFavorite")
        //siempre devuelve false
        return isFavorite
    }



    private fun loadUserFromPreferences(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        Log.d("UserPrefs", "Usuario cargado: $username")
        return username
    }
}
