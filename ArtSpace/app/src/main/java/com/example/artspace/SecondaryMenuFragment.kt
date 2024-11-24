package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.artspace.adapter.MainMenuAdapter
import com.example.artspace.adapter.SecondaryMenuAdapter
import com.example.artspace.data.MainMenuData
import com.example.artspace.data.SecondaryMenuData
import com.example.artspace.databinding.FragmentSecondaryMenuBinding

class SecondaryMenuFragment : Fragment(R.layout.item_secondary_menu) {
    // Declaramos el binding
    private var _binding: FragmentSecondaryMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout con ViewBinding
        _binding = FragmentSecondaryMenuBinding.inflate(inflater, container, false)

        val dataset = SecondaryMenuData().LoadMenuItem()
        val recyclerView = binding.secondaryRecycler

        recyclerView.adapter = SecondaryMenuAdapter(this, dataset)
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}