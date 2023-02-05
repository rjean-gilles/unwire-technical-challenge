package com.rjeangilles.unwire.challenge

import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

private val log: Logger = LoggerFactory.getLogger("JourneyRepositoryPerfTests")

/**
 * Performance Tests on Journey repository
 */
@SpringBootTest
@AutoConfigureWebTestClient
class JourneyRepositoryPerfTests : AbstractJourneyPerfTests() {

    private suspend fun <R> suspAssertDoesNotThrow(executable: suspend () -> R): R {
        return try {
            executable()
        } catch(ex: Throwable) {
            // assert* only take non-suspendable functions,
            // so as a workaround we do a catch+rethrow to
            // implement a "assertDoesNotThrow" on suspendable code
            assertDoesNotThrow {
                throw ex
            }
        }
    }

    @Test
    fun getAllJourneysByUserIdPerfTest() {
        runPerfTestQueries("getAllJourneysByUserIdPerfTest"){ rng, jobIndex, requestIndex ->
            log.info("Sending request #{} from job #{}", requestIndex, jobIndex)
            val userId = dataLoader.randomGeneratedUserId(rng)
            suspAssertDoesNotThrow {
                journeyRepository.findAllByUserId(userId)
            }
        }
    }

    @Test
    fun getJourneyByIdPerfTest() {
        runPerfTestQueries("getJourneyByIdPerfTest"){ rng, _, _ ->
            val journeyId = dataLoader.randomGeneratedJourneyId(rng)
            suspAssertDoesNotThrow {
                journeyRepository.findById(journeyId)
            }
        }
    }
}