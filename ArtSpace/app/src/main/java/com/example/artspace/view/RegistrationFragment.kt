package com.example.artspace.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.artspace.R
import com.example.artspace.data.AuthManager
import com.example.artspace.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        authManager = AuthManager()

        binding.fab.setOnClickListener {
            val email = binding.userEmail.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authManager.registerUser(email, password,
                    onSuccess = {
                        saveUserToPreferences(email)
                        findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        val message = when (exception) {
                            is FirebaseAuthUserCollisionException -> getString(R.string.user_already_exists)
                            else -> exception?.localizedMessage ?: getString(R.string.error_registering)
                        }
                        Log.e("AUTH_DEBUG", "Error al registrar: $message", exception)
                        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), getString(R.string.toastEmpty), Toast.LENGTH_SHORT).show()
            }
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
