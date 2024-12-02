package com.example.artspace.model

data class User(
    val username: String,
    val password: String,
    var favorites: HashSet<String> = HashSet() // Inicializamos un HashSet vacío por defecto
) {
    constructor(username: String, password: String) : this(username, password, HashSet())


}

