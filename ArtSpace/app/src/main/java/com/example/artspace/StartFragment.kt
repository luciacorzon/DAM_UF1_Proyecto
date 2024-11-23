package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.artspace.databinding.FragmentStartBinding

class StartFragment : Fragment(R.layout.fragment_start) {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)

        // Detecta un toque en cualquier parte de la vista y navega al siguiente fragmento
        binding.root.setOnClickListener {
            // Aquí navegas al siguiente fragmento cuando tocas en cualquier parte
            findNavController().navigate(R.id.action_startFragment_to_mainMenuFragment2)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}