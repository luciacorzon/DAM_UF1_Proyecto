package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.artspace.data.javaClasses.UserDAO
import com.example.artspace.databinding.FragmentRegistrationBinding
import com.example.artspace.model.User
import com.example.artspace.service.UserStorage

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener{
            val username = binding.userName.text.toString()
            val password = binding.password.text.toString()

            val userDAO = UserDAO(requireContext())

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = User(username, password)

                val existingUser = userDAO.getByName(username)

                if (existingUser != null) {
                    if (existingUser.password != password) {
                        Toast.makeText(requireContext(), R.string.toastPassword, Toast.LENGTH_SHORT).show()
                    } else {
                        userDAO.save(user)
                        findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                    }
                } else {
                    // El usuario no existe, se registra
                    userDAO.save(user)
                    findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                }
            } else {
                Toast.makeText(requireContext(), R.string.toastEmpty, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}