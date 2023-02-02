package com.rjeangilles.unwire.challenge.repository

import com.rjeangilles.unwire.challenge.model.*
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Repository
class JourneyRepositoryImpl : JourneyRepository {
    // FIXME: we just synchronize on a lock for now, we'll rework this later
    private val lock = Any()

    private val userIdToJourneys: MutableMap<UserId, MutableList<Journey>> = mutableMapOf()
    private val journeyIdToJourney: MutableMap<JourneyId, Journey> = mutableMapOf()
    private val journeyIdToUserId: MutableMap<JourneyId, UserId> = mutableMapOf()

    private val journeyIdCounter = AtomicLong()

    fun generateJourneyId(): JourneyId = journeyIdCounter.incrementAndGet()

    private fun insert(userId: UserId, newJourney: Journey) {
        val newJourneyId = requireNotNull(newJourney.id)
        synchronized(lock) {
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

    override fun create(userId: UserId, newJourney: Journey): Journey {
        require(newJourney.id == null)
        val insertedJourney = newJourney.copy(id = generateJourneyId())
        insert(userId, insertedJourney)
        return insertedJourney
    }

    private fun createAll(userId: UserId, vararg newJourneys: Journey) {
        for (newJourney in newJourneys) {
            create(userId, newJourney)
        }
    }

    override fun findAllByUserId(userId: UserId): Iterable<Journey> {
        return synchronized(lock) {
            userIdToJourneys.get(userId) ?: listOf()
        }
    }

    override fun findById(journeyId: JourneyId): Optional<Journey> {
        return synchronized(lock) {
            Optional.ofNullable(journeyIdToJourney.get(journeyId))
        }
    }

    override fun existsById(journeyId: JourneyId): Boolean {
        return synchronized(lock) {
            journeyIdToJourney.containsKey(journeyId)
        }
    }

    override fun deleteById(journeyId: JourneyId) {
        synchronized(lock) {
            journeyIdToJourney.remove(journeyId)
            val userId = journeyIdToUserId.remove(journeyId)
            if (userId != null) {
                userIdToJourneys.remove(userId)
            }
        }
    }

    override fun deleteAllByUserId(userId: UserId) {
        synchronized(lock) {
            val journeys = userIdToJourneys.remove(userId)
            if (journeys != null) {
                for (journey in journeys) {
                    journeyIdToJourney.remove(journey.id)
                    journeyIdToUserId.remove(journey.id)
                }
            }
        }
    }


    // TEST
    // TODO: remove me
    init{
        createAll(
            1,
            Journey(
                startAddress = "1959 NE Pacific St",
                endAddress = "2100 W Genesee Turnpike",
                steps = listOf(
                    Step(
                        TravelMode.WALK,
                        Instant.parse("2023-01-01T12:49:27Z"),
                        Location(
                            103,
                            GeoPoint(43.547165, -106.626785)
                        ),
                        Instant.parse("2023-01-01T12:55:05Z"),
                        Location(
                            215,
                            GeoPoint(43.991442, -97.837722)
                        )
                    ),
                    Step(
                        TravelMode.SUBWAY,
                        Instant.parse("2023-01-01T12:56:33Z"),
                        Location(
                            103,
                            GeoPoint(43.551234, -106.633333)
                        ),
                        Instant.parse("2023-01-01T13:10:12Z"),
                        Location(
                            215,
                            GeoPoint(43.998163, -97.836534)
                        )
                    )
                )
            ),
            Journey(
                startAddress = "1620 S Mission St",
                endAddress = "1880 Willamette Falls Dr",
                steps = listOf(
                    Step(
                        TravelMode.BICYCLE,
                        Instant.parse("2023-01-01T15:15:33Z"),
                        Location(
                            203,
                            GeoPoint(43.551234, -106.633333)
                        ),
                        Instant.parse("2023-01-01T13:10:12Z"),
                        Location(
                            335,
                            GeoPoint(43.998163, -97.836534)
                        )
                    )
                )
            )
        )
        createAll(
            2,
            Journey(
                startLocation = Location(
                    null,
                    GeoPoint(43.547165, -106.626785)
                ),
                endAddress = "1880 Willamette Falls Dr",
                steps = listOf(
                    Step(
                        TravelMode.WALK,
                        Instant.parse("2023-01-02T12:49:27Z"),
                        Location(
                            null,
                            GeoPoint(43.547165, -106.626785)
                        ),
                        Instant.parse("2023-01-02T12:55:05Z"),
                        Location(
                            null,
                            GeoPoint(43.95, -106.41)
                        )
                    ),
                    Step(
                        TravelMode.RAIL,
                        Instant.parse("2023-01-02T12:56:33Z"),
                        Location(
                            71,
                            GeoPoint(43.856, -106.512)
                        ),
                        Instant.parse("2023-01-02T13:10:12Z"),
                        Location(
                            215,
                            GeoPoint(43.965, -106.432)
                        )
                    ),
                    Step(
                        TravelMode.WALK,
                        Instant.parse("2023-01-02T13:15:35Z"),
                        Location(
                            null,
                            GeoPoint(43.843, -106.298)
                        ),
                        Instant.parse("2023-01-02T13:33:56Z"),
                        Location(
                            null,
                            GeoPoint(43.754, -106.3652)
                        )
                    ),
                )
            )
        )
    }

}