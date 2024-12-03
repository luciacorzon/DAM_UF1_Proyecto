package com.example.artspace.model

data class User(
    val username: String,
    val password: String,
    var favorites: HashSet<String> = HashSet()
) {
    constructor(username: String, password: String) : this(username, password, HashSet())


}

