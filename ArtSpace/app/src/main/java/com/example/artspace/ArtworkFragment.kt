package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ArtworkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artwork, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_favorite)
        // Usa el contexto de requireContext() para obtener el drawable
        val drawable = ContextCompat.getDrawable(requireContext(),
            R.drawable.ic_baseline_favorite_border_40
        )

        // Ajusta el tamaño del drawable aquí, si es necesario
        drawable?.let {
            // Por ejemplo, establecer el tamaño del drawable usando setBounds
            it.setBounds(0, 0, 120, 120) // Ajusta el tamaño a tu necesidad
            fab.setImageDrawable(it) // Establecer el drawable como imagen del FAB
        }

        return view
    }
}