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
class JourneyRepositoryImpl : JourneyRepository {
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

    private val userIdToJourneys: MutableMap<UserId, MutableList<Journey>> = mutableMapOf()
    private val journeyIdToJourney: MutableMap<JourneyId, Journey> = mutableMapOf()
    private val journeyIdToUserId: MutableMap<JourneyId, UserId> = mutableMapOf()

    private val journeyIdCounter = AtomicLong()

    private fun generateJourneyId(): JourneyId = journeyIdCounter.incrementAndGet()

    private suspend fun insert(userId: UserId, newJourney: Journey) {
        val newJourneyId = requireNotNull(newJourney.id)
        withWriteLock {
            var list = userIdToJourneys.get(userId)
            if (list == null) {
                list = mutableListOf<Journey>()
                userIdToJourneys.put(userId, list)
            }
            list.add(newJourney)

            journeyIdToJourney.put(newJourneyId, newJourney)

            journeyIdToUserId.put(newJourneyId, userId)
        }
    }

    override suspend fun create(userId: UserId, newJourney: Journey): Journey {
        require(newJourney.id == null)
        val insertedJourney = newJourney.copy(id = generateJourneyId())
        insert(userId, insertedJourney)
        return insertedJourney
    }

    override suspend fun findAllByUserId(userId: UserId): Iterable<Journey> {
        return withReadLock {
            userIdToJourneys.get(userId) ?: listOf()
        }
    }

    override suspend fun findById(journeyId: JourneyId): Optional<Journey> {
        return withReadLock {
            Optional.ofNullable(journeyIdToJourney.get(journeyId))
        }
    }

    override suspend fun existsById(journeyId: JourneyId): Boolean {
        return withReadLock {
            journeyIdToJourney.containsKey(journeyId)
        }
    }

    override suspend fun deleteById(journeyId: JourneyId) {
        withWriteLock {
            journeyIdToJourney.remove(journeyId)
            val userId = journeyIdToUserId.remove(journeyId)
            if (userId != null) {
                userIdToJourneys.remove(userId)
            }
        }
    }

    override suspend fun deleteAllByUserId(userId: UserId) {
        withWriteLock {
            val journeys = userIdToJourneys.remove(userId)
            if (journeys != null) {
                for (journey in journeys) {
                    journeyIdToJourney.remove(journey.id)
                    journeyIdToUserId.remove(journey.id)
                }
            }
        }
    }

    override suspend fun deleteAll() {
        withWriteLock {
            userIdToJourneys.clear()
            journeyIdToJourney.clear()
            journeyIdToUserId.clear()
        }
    }
}