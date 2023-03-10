package com.rjeangilles.unwire.challenge.controller

import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import com.rjeangilles.unwire.challenge.service.JourneyService
import org.springframework.web.bind.annotation.*


@RestController
class JourneyController(
    private val service: JourneyService
) {
    @GetMapping("/user/{userId}/journeys")
    suspend fun getAllByUserId(@PathVariable userId: UserId): Iterable<Journey> {
        return service.getAllByUserId(userId)
    }

    @GetMapping("/journey/{journeyId}")
    suspend fun getById(@PathVariable journeyId: JourneyId): Journey {
        return service.getById(journeyId)
    }

    @PostMapping("/user/{userId}/journeys")
    suspend fun create(@PathVariable userId: UserId, @RequestBody newJourney: Journey): Journey {
        return service.create(userId, newJourney)
    }

    @DeleteMapping("/user/{userId}/journeys")
    suspend fun deleteAllByUserId(@PathVariable userId: UserId) {
        service.deleteAllByUserId(userId)
    }

    @DeleteMapping("/journey/{journeyId}")
    suspend fun deleteById(@PathVariable journeyId: UserId) {
        service.deleteById(journeyId)
    }
}