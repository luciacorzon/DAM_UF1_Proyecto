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

        binding.mapButton.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_mainMenuFragment2_to_mapFragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val dataset = MainMenuData().LoadMenuItem()
        val recyclerView = binding.mainRecycler

        val gridLayoutManager = GridLayoutManager(context, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = MainMenuAdapter(this, dataset) { item ->
            onMenuItemClicked(item)
        }

        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    private fun onMenuItemClicked(item: MenuItem) {
        when (item.nameRes) {
            R.string.discipline_category -> {
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
