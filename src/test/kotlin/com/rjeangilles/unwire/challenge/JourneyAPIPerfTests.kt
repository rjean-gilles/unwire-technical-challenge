package com.rjeangilles.unwire.challenge

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

private val log: Logger = LoggerFactory.getLogger("JourneyAPIPerfTests")

/**
 * Performance Tests on the Journey API (at the  Web Layer, doing actual HTTP requests)
 */
@SpringBootTest/*(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)*/
@AutoConfigureWebTestClient
class JourneyAPIPerfTests : AbstractJourneyPerfTests() {
    @Autowired private lateinit var webTestClient: WebTestClient

    //@LocalServerPort private var serverPort: Int = 0

    @Test
    fun getAllJourneysByUserIdPerfTest() {
        runPerfTestQueries("getAllJourneysByUserIdPerfTest"){ rng, jobIndex, requestIndex ->
            log.info("Sending request #{} from job #{}", requestIndex, jobIndex)
            val userId = dataLoader.randomGeneratedUserId(rng)
            webTestClient.get().uri("/user/${userId}/journeys")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("\$").isArray()

        }
    }

    @Test
    fun getJourneyByIdPerfTest() {
        runPerfTestQueries("getJourneyByIdPerfTest"){ rng, _, _ ->
            val journeyId = dataLoader.randomGeneratedJourneyId(rng)
            webTestClient.get().uri("/journey/${journeyId}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("\$").isMap()
        }
    }
}