package com.rjeangilles.unwire.challenge

import com.rjeangilles.unwire.challenge.model.*
import com.rjeangilles.unwire.challenge.repository.test.LoadTestingDataLoader
import com.rjeangilles.unwire.challenge.service.JourneyService
import kotlinx.coroutines.*
import org.instancio.Instancio
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import java.util.concurrent.atomic.AtomicLong

private val log: Logger = LoggerFactory.getLogger("PerfTests")

@SpringBootTest/*(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)*/
@AutoConfigureWebTestClient
class PerfTests {
    // How many API calls we'll perform in the performance test
    private val perfTestApiQueryCount = 20_000L
    // Number of concurrent requests
    private val concurrentRequestCount: Int = 10

    @Autowired private lateinit var journeyService: JourneyService
    @Autowired private lateinit var webTestClient: WebTestClient

    private lateinit var dataLoader: LoadTestingDataLoader

    //@LocalServerPort private var serverPort: Int = 0

    @BeforeEach
    fun setUp() {
        runBlocking {
            journeyService.deleteAll()
            dataLoader = LoadTestingDataLoader(journeyService)
            dataLoader.load()
        }
    }

    private fun runTestQueries(
        jobCount: Int = concurrentRequestCount,
        requestCount: Long = perfTestApiQueryCount,
        block: (Random, Int, Long) -> Unit
    ) {
        val sentRequestCount = AtomicLong(0L)
        //runTest(UnconfinedTestDispatcher) {
        //runBlocking(Dispatchers.Default) {
        runBlocking(Dispatchers.IO/*.limitedParallelism(32)*/){
        //runBlocking {
            val jobs: List<Job> = List(jobCount) { jobIndex ->
                launch {
                    val rng = Random(jobIndex.toLong())
                    var requestIndex = sentRequestCount.getAndIncrement()
                    while(requestIndex < requestCount) {
                        block(rng, jobIndex, requestIndex)
                        requestIndex = sentRequestCount.getAndIncrement()
                    }
                }
            }

            jobs.joinAll()
        }
    }

    @Test
    fun getAllJourneysByUserIdPerfTest() {
        runTestQueries{ rng, jobIndex, requestIndex ->
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
        runTestQueries{ rng, _, _ ->
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

    @AfterEach
    fun tearDown() {
        runBlocking {
            journeyService.deleteAll()
        }
    }
}