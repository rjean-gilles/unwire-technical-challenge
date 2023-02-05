package com.rjeangilles.unwire.challenge.service

import com.rjeangilles.unwire.challenge.model.User
import com.rjeangilles.unwire.challenge.model.UserId
import com.rjeangilles.unwire.challenge.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override suspend fun create(newUser: User): User {
        if (newUser.id != null) {
            throw UserIdNotNullException(newUser.id)
        }
        return userRepository.create(newUser)
    }
    override suspend fun getById(userId: UserId): User {
        return userRepository.findById(userId)
            .orElseThrow(Supplier<RuntimeException> { UserNotFoundException(userId) })
    }
    override suspend fun existsById(userId: UserId): Boolean {
        return userRepository.existsById(userId)
    }
    override suspend fun deleteById(userId: UserId) {
        userRepository.deleteById(userId)
    }
    override suspend fun deleteAll() {
        userRepository.deleteAll()
    }

}