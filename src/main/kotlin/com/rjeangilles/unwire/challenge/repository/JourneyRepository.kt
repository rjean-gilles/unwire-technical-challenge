package com.rjeangilles.unwire.challenge.repository

import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

interface JourneyRepository {
    suspend fun create(userId: UserId, newJourney: Journey): Journey
    suspend fun findAllByUserId(userId: UserId): Iterable<Journey>
    suspend fun findById(journeyId: JourneyId): Optional<Journey>
    suspend fun existsById(journeyId: JourneyId): Boolean
    suspend fun deleteById(journeyId: JourneyId)
    suspend fun deleteAllByUserId(userId: UserId)
    suspend fun deleteAll()
}