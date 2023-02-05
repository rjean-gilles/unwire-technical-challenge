package com.rjeangilles.unwire.challenge.service

import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId
import com.rjeangilles.unwire.challenge.repository.JourneyRepository
import com.rjeangilles.unwire.challenge.repository.UserRepository
import com.rjeangilles.unwire.challenge.repository.test.LoadTestingDataLoader
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
class JourneyServiceImpl(
    private val userRepository: UserRepository,
    private val journeyRepository: JourneyRepository
) : JourneyService {

    @PostConstruct
    fun initialize() {
        if (System.getProperty("load-test-data", "false") == "true") {
            LoadTestingDataLoader(userRepository, journeyRepository).load()
        }
    }

    override suspend fun create(userId: UserId, newJourney: Journey): Journey {
        if (newJourney.id != null) {
            throw JourneyIdNotNullException(newJourney.id)
        }
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException(userId)
        }
        return journeyRepository.create(userId, newJourney)
    }
    override suspend fun getAllByUserId(userId: UserId): Iterable<Journey> {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException(userId)
        }
        return journeyRepository.findAllByUserId(userId)
    }
    override suspend fun getById(journeyId: JourneyId): Journey {
        return journeyRepository.findById(journeyId)
            .orElseThrow(Supplier<RuntimeException> { JourneyNotFoundException(journeyId) })
    }
    override suspend fun existsById(journeyId: JourneyId): Boolean {
        return journeyRepository.existsById(journeyId)
    }
    override suspend fun deleteById(journeyId: JourneyId) {
        journeyRepository.deleteById(journeyId)
    }
    override suspend fun deleteAllByUserId(userId: UserId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException(userId)
        }
        journeyRepository.deleteAllByUserId(userId)
    }
    override suspend fun deleteAll() {
        journeyRepository.deleteAll()
    }

}