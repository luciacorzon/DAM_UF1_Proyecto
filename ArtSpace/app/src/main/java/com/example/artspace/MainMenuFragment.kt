package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.artspace.adapter.MainMenuAdapter
import com.example.artspace.databinding.FragmentMainMenuBinding
import com.example.artspace.data.MainMenuData
import com.example.artspace.model.MenuItem

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    // Declaramos el binding
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout con ViewBinding
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

       val dataset = MainMenuData().LoadMenuItem()
        val recyclerView = binding.mainRecycler

        //recyclerView.adapter = MainMenuAdapter(this, dataset)

        //AÑADIDO
        recyclerView.adapter = MainMenuAdapter(this, dataset) { item ->
            // Aquí manejas el clic en el ítem, según el id o el tipo de ítem
            onMenuItemClicked(item)
        }

        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    private fun onMenuItemClicked(item: MenuItem) {
        when (item.nameRes) {
            R.string.online_gallery -> {
                // Navegar a la opción 1
                findNavController().navigate(R.id.action_mainMenuFragment2_to_secondaryMenuFragment)
            }
            R.string.your_gallery -> {
                // Navegar a la opción 2
                findNavController().navigate(R.id.action_mainMenuFragment2_to_galleryFragment)
            }
            // Agregar más casos si es necesario
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}