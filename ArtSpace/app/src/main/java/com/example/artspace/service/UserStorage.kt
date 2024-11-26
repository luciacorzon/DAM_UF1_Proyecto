package com.example.artspace.service

import android.content.Context
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.artspace.data.javaClasses.UserDAO
import com.example.artspace.model.User
import androidx.core.content.ContentProviderCompat.requireContext


class UserStorage(private val context: Context) {

    private var userDAO: UserDAO = UserDAO(context)

    fun saveUser(user: User) {
        userDAO.save(user)
        userDAO.printUsersFromFile();
    }

    fun getUserByUsername(username: String): User? {
        return userDAO.getById(username.hashCode())
    }

    fun deleteUser(username: String) {
        userDAO.deleteByName(username)
    }

    fun updateUser(user: User) {
        userDAO.update(user)
    }

    fun getAllUsers(): List<User> {
        return userDAO.getUsersList()
    }
}