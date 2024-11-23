package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.artspace.adapter.MainMenuAdapter
import com.example.artspace.databinding.FragmentMainMenuBinding
import com.example.artspace.model.MainMenuData

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

        recyclerView.adapter = MainMenuAdapter(this, dataset)
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}