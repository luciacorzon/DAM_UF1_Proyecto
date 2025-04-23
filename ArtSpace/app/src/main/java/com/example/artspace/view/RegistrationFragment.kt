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
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        authManager = AuthManager()

        binding.loginBtn.setOnClickListener {
            val email = binding.userName.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authManager.registerUser(email, password,
                    onSuccess = {
                        saveUserToPreferences(email)
                        findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                        Log.d("AUTH_DEBUG", "Usuario registrado exitosamente")
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        if (exception is FirebaseAuthUserCollisionException) {
                            authManager.signInUser(email, password,
                                onSuccess = {
                                    saveUserToPreferences(email)
                                    findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                                    Log.d("AUTH_DEBUG", "Inicio de sesión exitoso")
                                    Toast.makeText(requireContext(), "Sesión iniciada", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { loginError ->
                                    val message = loginError?.localizedMessage ?: "Error desconocido al iniciar sesión"
                                    Log.d("AUTH_DEBUG", "Error al iniciar sesión: $message")
                                    Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            val message = exception?.localizedMessage ?: "Error desconocido al registrar"
                            Log.d("AUTH_DEBUG", "Error al registrar: $message")
                            Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            } else {
                val emptyMsg = getString(R.string.toastEmpty)
                Log.d("AUTH_DEBUG", "Campos vacíos: $emptyMsg")
                Toast.makeText(requireContext(), emptyMsg, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun saveUserToPreferences(email: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", email)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
