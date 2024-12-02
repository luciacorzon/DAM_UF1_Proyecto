package com.example.artspace

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artspace.adapter.MainMenuAdapter
import com.example.artspace.databinding.FragmentMainMenuBinding
import com.example.artspace.data.MainMenuData
import com.example.artspace.decorations.BottomBorderDecoration
import com.example.artspace.model.MenuItem

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    // Declaramos el binding
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

       val dataset = MainMenuData().LoadMenuItem()
        val recyclerView = binding.mainRecycler

        val gridLayoutManager = GridLayoutManager(context, 2) // Dos filas
        gridLayoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = gridLayoutManager

        recyclerView.adapter = MainMenuAdapter(this, dataset) { item ->
            onMenuItemClicked(item)
        }

        //AÑADIDO
        val bottomBorderDecoration = BottomBorderDecoration(Color.GREEN, 4)
        recyclerView.addItemDecoration(bottomBorderDecoration)


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