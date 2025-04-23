package com.example.artspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.artspace.data.AuthManager
import com.example.artspace.databinding.FragmentLoginBinding
import android.content.Context


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        authManager = AuthManager()

        binding.fab.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authManager.signInUser(email, password,
                    onSuccess = {
                        saveUserToPreferences(email)
                        findNavController().navigate(R.id.action_loginFragment_to_mainMenuFragment2)
                        Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        val message = exception?.localizedMessage ?: "Error al iniciar sesión"
                        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), getString(R.string.toastEmpty), Toast.LENGTH_SHORT).show()
            }
        }

        binding.anonymousLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_mainMenuFragment2)
            Toast.makeText(requireContext(), "Ingresaste como invitado", Toast.LENGTH_SHORT).show()
        }

        binding.redirectionMsg.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }


        return binding.root
    }

    private fun saveUserToPreferences(email: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("username", email).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}