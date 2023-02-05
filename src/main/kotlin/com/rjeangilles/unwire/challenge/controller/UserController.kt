package com.rjeangilles.unwire.challenge.controller

import com.rjeangilles.unwire.challenge.model.User
import com.rjeangilles.unwire.challenge.model.UserId
import com.rjeangilles.unwire.challenge.service.UserService
import org.springframework.web.bind.annotation.*


@RestController
class UserController(
    private val service: UserService
) {
    @GetMapping("/user/{userId}")
    suspend fun getById(@PathVariable userId: UserId): User {
        return service.getById(userId)
    }

    @PostMapping("/user")
    suspend fun create(@RequestBody newUser: User): User {
        return service.create(newUser)
    }
    
    @DeleteMapping("/user/{userId}")
    suspend fun deleteById(@PathVariable userId: UserId) {
        service.deleteById(userId)
    }
}