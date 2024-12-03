package com.example.artspace.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.artspace.R
import com.example.artspace.data.javaClasses.UserDAO
import com.example.artspace.databinding.FragmentRegistrationBinding
import com.example.artspace.model.User

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
                        saveUserToPreferences(username)
                        findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                    }
                } else {
                    userDAO.save(user)
                    saveUserToPreferences(username)
                    findNavController().navigate(R.id.action_registrationFragment_to_mainMenuFragment2)
                }
            } else {
                Toast.makeText(requireContext(), R.string.toastEmpty, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun saveUserToPreferences(username: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}