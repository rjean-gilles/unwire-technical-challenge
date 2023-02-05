package com.rjeangilles.unwire.challenge.repository

import com.rjeangilles.unwire.challenge.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Repository
class UserRepositoryImpl : UserRepository {
    // Ideally we would like to use a ReadWrite Lock,
    // but the standard library does not provide one
    // for coroutines, so we settle for a Mutex.
    private val mutex = Mutex()
    private suspend inline fun <T> withReadLock(owner: Any? = null, action: () -> T): T {
        return mutex.withLock(owner, action)
    }
    private suspend inline fun <T> withWriteLock(owner: Any? = null, action: () -> T): T {
        return mutex.withLock(owner, action)
    }

    private val userIdToUser: MutableMap<UserId, User> = mutableMapOf()

    private val userIdCounter = AtomicLong()

    private fun generateUserId(): UserId = userIdCounter.incrementAndGet()

    private suspend fun insert(newUser: User) {
        val newUserId = requireNotNull(newUser.id)
        withWriteLock {
            userIdToUser.put(newUserId, newUser)
        }
    }

    override suspend fun create(newUser: User): User {
        require(newUser.id == null)
        val insertedUser = newUser.copy(id = generateUserId())
        insert(insertedUser)
        return insertedUser
    }

    override suspend fun findById(userId: UserId): Optional<User> {
        return withReadLock {
            Optional.ofNullable(userIdToUser.get(userId))
        }
    }

    override suspend fun existsById(userId: UserId): Boolean {
        return withReadLock {
            userIdToUser.containsKey(userId)
        }
    }

    override suspend fun deleteById(userId: UserId) {
        withWriteLock {
            userIdToUser.remove(userId)
        }
    }

    override suspend fun deleteAll() {
        withWriteLock {
            userIdToUser.clear()
        }
    }
}