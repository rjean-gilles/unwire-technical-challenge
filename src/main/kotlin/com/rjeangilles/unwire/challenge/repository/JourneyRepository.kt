package com.rjeangilles.unwire.challenge.repository

import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

interface JourneyRepository {
    fun create(userId: UserId, newJourney: Journey): Journey
    fun findAllByUserId(userId: UserId): Iterable<Journey>
    fun findById(journeyId: JourneyId): Optional<Journey>
    fun existsById(journeyId: JourneyId): Boolean
    fun deleteById(journeyId: JourneyId)
    fun deleteAllByUserId(userId: JourneyId)
}