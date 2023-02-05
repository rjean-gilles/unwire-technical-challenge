package com.rjeangilles.unwire.challenge.repository.test

import com.rjeangilles.unwire.challenge.model.*
import com.rjeangilles.unwire.challenge.repository.JourneyRepository
import com.rjeangilles.unwire.challenge.repository.UserRepository
import kotlinx.coroutines.*
import org.instancio.Instancio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicLong

private val log: Logger = LoggerFactory.getLogger("LoadTestingDataLoader")

/**
 * Generates Journey instances suitable for tests
 */
private class TestJourneyGenerator(
    private val journeyRepository: JourneyRepository
) {
    private val templates = arrayOfNulls<Journey>(10)
    private val rng: Random = Random(123L)

    init {
        // Instancio is convenient to easily generate random instances,
        // but is very slow so we just generate a handful of instances
        // that we will then just copy
        for (i in 0 until templates.size) {
            templates[i] = Instancio.of(Journey::class.java)
                .withSeed(i.toLong())
                .create()
                .copy(id = null)
        }
    }

    suspend fun create(userId: UserId): Journey {
        return journeyRepository.create(
            userId,
            templates[rng.nextInt(templates.size)]!!
        )
    }
}

/**
 * Generates User instances suitable for tests
 */
private class TestUserGenerator(
    private val userRepository: UserRepository,
    private val maxJourneys: Int
) {
    private val rng: Random = Random(456L)
    private val generatedCount = AtomicLong()

    suspend fun create(): User {
        return userRepository.create(
            User(
                name = "Generated User #" + generatedCount.incrementAndGet()
            )
        )
    }
    suspend fun journeyCount(): Int {
        return rng.nextInt(maxJourneys)
    }
}

/**
 * Loads test data into the repositories,
 * for use before performing load testing.
 */
class LoadTestingDataLoader(
    private val userRepository: UserRepository,
    private val journeyRepository: JourneyRepository,
    // The number of users we generate as a fixture for the performance test
    private val generatedUserCount: Int = 1_000_000,
    // The maximum number of journeys we generate per use
    private val maxGeneratedJourneyCountPer: Int = 15
) {
    val generatedUserIds: List<UserId> get() = _generatedUserIds
    private val _generatedUserIds = arrayListOf<UserId>()

    val generatedJourneyIds: List<JourneyId> get() = _generatedJourneyIds
    private val _generatedJourneyIds = arrayListOf<JourneyId>()

    private val userGenerator = TestUserGenerator(userRepository, maxGeneratedJourneyCountPer)
    private val journeyGenerator = TestJourneyGenerator(journeyRepository)

    fun load() {
        // We want to make sure that the data is loaded before accepting
        // queries, so we have to block. It is bad, but acceptable given
        // that this is only for (performance) tests.
        runBlocking {
            for (u in 0 until generatedUserCount) {
                if (u % 10000 == 0) {
                    log.info("Generating users: {} / {}", u, generatedUserCount)
                }
                val generatedUser: User = userGenerator.create()
                _generatedUserIds.add(generatedUser.id!!)
                val generatedJourneyCount = userGenerator.journeyCount()
                for (j in 1..generatedJourneyCount) {
                    val journey = journeyGenerator.create(generatedUser.id!!)
                    _generatedJourneyIds.add(journey.id!!)
                }
            }
        }
    }

    /**
     * Returns a randomly chosen journey id among all the journeys that
     * have been generated and loaded into the repository
     */
    fun randomGeneratedJourneyId(rng: Random): JourneyId {
        return _generatedJourneyIds[rng.nextInt(_generatedJourneyIds.size)]
    }

    /**
     * Returns a randomly chosen user id among all the users that
     * have been generated and loaded into the repository.
     */
    fun randomGeneratedUserId(rng: Random): UserId {
        return _generatedUserIds[rng.nextInt(_generatedUserIds.size)]
    }
}