package com.rjeangilles.unwire.challenge.service

import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

interface JourneyService {
    suspend fun create(userId: UserId, newJourney: Journey): Journey
    suspend fun getAllByUserId(userId: UserId): Iterable<Journey>
    suspend fun getById(journeyId: JourneyId): Journey
    suspend fun deleteById(journeyId: JourneyId)
    suspend fun deleteAllByUserId(userId: UserId)
    suspend fun deleteAll()
}