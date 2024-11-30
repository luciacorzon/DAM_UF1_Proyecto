package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.adapter.MainMenuAdapter
import com.example.artspace.adapter.SecondaryMenuAdapter
import com.example.artspace.data.MainMenuData
import com.example.artspace.data.SecondaryMenuData
import com.example.artspace.databinding.FragmentSecondaryMenuBinding
import com.example.artspace.viewmodels.ArtViewModel

class SecondaryMenuFragment : Fragment(R.layout.fragment_secondary_menu) {
    private var _binding: FragmentSecondaryMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ArtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondaryMenuBinding.inflate(inflater, container, false)

        val dataset = SecondaryMenuData().LoadMenuItem()
        val recyclerView = binding.secondaryRecycler

        recyclerView.adapter = SecondaryMenuAdapter(this, dataset) { menuItem ->
            onMenuItemClick(menuItem) // Llama a la función que maneja el clic
        }

        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    private fun onMenuItemClick(menuItem: String) {
        // Recuperamos las categorías de los recursos de cadenas
        val category = when (menuItem) {
            getString(R.string.paint) -> getString(R.string.paint)
            getString(R.string.sculpture) -> getString(R.string.sculpture)
            getString(R.string.photography) -> getString(R.string.photography)
            else -> "Unknown"
        }

        val bundle = Bundle().apply {
            putString("category", category) // Pasar la categoría seleccionada
        }

        // Navegar a GalleryFragment con el Bundle
        findNavController().navigate(R.id.action_secondaryMenuFragment_to_galleryFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


