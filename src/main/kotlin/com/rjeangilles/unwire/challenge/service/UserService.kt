package com.rjeangilles.unwire.challenge.service

import com.rjeangilles.unwire.challenge.model.User
import com.rjeangilles.unwire.challenge.model.UserId

interface UserService {
    suspend fun create(newUser: User): User
    suspend fun getById(userId: UserId): User
    suspend fun existsById(userId: UserId): Boolean
    suspend fun deleteById(userId: UserId)
    suspend fun deleteAll()
}