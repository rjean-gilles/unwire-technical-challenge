package com.rjeangilles.unwire.challenge.service

import com.rjeangilles.unwire.challenge.controller.exception.JourneyIdNotNullException
import com.rjeangilles.unwire.challenge.controller.exception.JourneyNotFoundException
import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import com.rjeangilles.unwire.challenge.repository.JourneyRepository
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
class JourneyServiceImpl(
    private val repository: JourneyRepository
) : JourneyService {
    override fun create(userId: UserId, newJourney: Journey): Journey {
        if (newJourney.id != null) {
            throw JourneyIdNotNullException(newJourney.id)
        }
        return repository.create(userId, newJourney)
    }
    override fun getAllByUserId(userId: UserId): Iterable<Journey> {
        // TODO: throw UserNotFoundException if user does not exist
        return repository.findAllByUserId(userId)
    }
    override fun getById(journeyId: JourneyId): Journey {
        return repository.findById(journeyId)
            .orElseThrow(Supplier<RuntimeException> { JourneyNotFoundException(journeyId) })
    }
    override fun deleteById(journeyId: JourneyId) {
        repository.deleteById(journeyId)
    }
    override fun deleteAllByUserId(userId: UserId) {
        repository.deleteAllByUserId(userId)
    }

}