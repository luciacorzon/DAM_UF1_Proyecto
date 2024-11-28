package com.example.artspace

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ArtworkFragment : Fragment() {

    private lateinit var fabFavorite: FloatingActionButton
    private var isFavorite: Boolean = false // Variable para controlar el estado del favorito

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout
        val view = inflater.inflate(R.layout.fragment_artwork, container, false)

        // Obtenemos el FloatingActionButton
        fabFavorite = view.findViewById(R.id.fab_favorite)

        // Comprobamos si el artículo está marcado como favorito
        isFavorite = loadFavoriteState()

        // Actualizamos el ícono según el estado cargado
        updateFabIcon()

        // Seteamos el listener para el clic en el FAB
        fabFavorite.setOnClickListener {
            // Invertimos el estado de favorito
            isFavorite = !isFavorite
            // Actualizamos el ícono del FAB
            updateFabIcon()
            // Guardamos el estado del favorito
            saveFavoriteState(isFavorite)
        }

        return view
    }

    // Función para actualizar el ícono del FAB según el estado
    private fun updateFabIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.baseline_favorite_24) // Ícono de corazón lleno
        } else {
            fabFavorite.setImageResource(R.drawable.baseline_favorite_border_24) // Ícono de corazón vacío
        }
    }

    // Guardar el estado en SharedPreferences
    private fun saveFavoriteState(isFavorite: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFavorite", isFavorite) // Guardamos el estado como booleano
        editor.apply()
    }

    // Cargar el estado desde SharedPreferences
    private fun loadFavoriteState(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFavorite", false) // Devuelve 'false' si no se encuentra el estado
    }
}
