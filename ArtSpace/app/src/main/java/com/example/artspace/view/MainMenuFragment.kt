package com.example.artspace.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.R
import com.example.artspace.adapter.MainMenuAdapter
import com.example.artspace.databinding.FragmentMainMenuBinding
import com.example.artspace.data.MainMenuData
import com.example.artspace.model.MenuItem

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        // ✅ Aquí sí está bien usar binding.mapButton
        binding.mapButton.setOnClickListener {
            try {
                Log.d("MainMenuFragment", "Botón presionado")
                Toast.makeText(requireContext(), "Click!", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_mainMenuFragment2_to_mapFragment)
            } catch (e: Exception) {
                Log.e("MainMenuFragment", "Error al navegar al MapFragment: ${e.message}")
                e.printStackTrace()
            }
        }

        val dataset = MainMenuData().LoadMenuItem()
        val recyclerView = binding.mainRecycler

        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = MainMenuAdapter(this, dataset) { item ->
            onMenuItemClicked(item)
        }

        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    private fun onMenuItemClicked(item: MenuItem) {
        when (item.nameRes) {
            R.string.online_gallery -> {
                findNavController().navigate(R.id.action_mainMenuFragment2_to_secondaryMenuFragment)
            }
            R.string.your_gallery -> {
                val bundle = Bundle().apply {
                    putString("category", "favorites")
                }
                findNavController().navigate(R.id.action_mainMenuFragment2_to_galleryFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
