package com.rjeangilles.unwire.challenge.service

import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

interface JourneyService {
    fun create(userId: UserId, newJourney: Journey): Journey
    fun getAllByUserId(userId: UserId): Iterable<Journey>
    fun getById(journeyId: JourneyId): Journey
    fun deleteById(journeyId: JourneyId)
    fun deleteAllByUserId(userId: UserId)
}