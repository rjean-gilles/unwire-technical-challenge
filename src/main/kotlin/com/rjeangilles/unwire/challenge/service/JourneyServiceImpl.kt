package com.rjeangilles.unwire.challenge.service

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
    override suspend fun create(userId: UserId, newJourney: Journey): Journey {
        if (newJourney.id != null) {
            throw JourneyIdNotNullException(newJourney.id)
        }
        return repository.create(userId, newJourney)
    }
    override suspend fun getAllByUserId(userId: UserId): Iterable<Journey> {
        // TODO: throw UserNotFoundException if user does not exist
        return repository.findAllByUserId(userId)
    }
    override suspend fun getById(journeyId: JourneyId): Journey {
        return repository.findById(journeyId)
            .orElseThrow(Supplier<RuntimeException> { JourneyNotFoundException(journeyId) })
    }
    override suspend fun deleteById(journeyId: JourneyId) {
        repository.deleteById(journeyId)
    }
    override suspend fun deleteAllByUserId(userId: UserId) {
        repository.deleteAllByUserId(userId)
    }

}