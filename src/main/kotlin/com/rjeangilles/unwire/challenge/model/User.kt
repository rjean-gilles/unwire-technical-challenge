package com.rjeangilles.unwire.challenge.model

typealias UserId = Long

// A minimalist User model, just so we can implement
// a User repository to be able to validate the existence
// of a given User when receiving queries
data class User(
    val id: UserId? = null,
    val name: String
)