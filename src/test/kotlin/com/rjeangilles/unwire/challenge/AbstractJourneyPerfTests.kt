package com.rjeangilles.unwire.challenge

import com.rjeangilles.unwire.challenge.model.*
import com.rjeangilles.unwire.challenge.repository.JourneyRepository
import com.rjeangilles.unwire.challenge.repository.UserRepository
import com.rjeangilles.unwire.challenge.repository.test.LoadTestingDataLoader
import kotlinx.coroutines.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.concurrent.atomic.AtomicLong

private val log: Logger = LoggerFactory.getLogger("AbstractJourneyPerfTests")

abstract class AbstractJourneyPerfTests {
    // How many calls we'll perform in the performance test
    protected val perfTestApiQueryCount = 20_000L
    // Number of concurrent requests
    protected val concurrentRequestCount: Int = 10

    @Autowired protected lateinit var userRepository: UserRepository
    @Autowired protected lateinit var journeyRepository: JourneyRepository

    protected lateinit var dataLoader: LoadTestingDataLoader

    @BeforeEach
    fun setUp() {
        runBlocking {
            journeyRepository.deleteAll()
            dataLoader = LoadTestingDataLoader(userRepository, journeyRepository)
            dataLoader.load()
        }
    }

    protected fun runPerfTestQueries(
        testName: String,
        jobCount: Int = concurrentRequestCount,
        requestCount: Long = perfTestApiQueryCount,
        block: suspend (Random, Int, Long) -> Unit
    ) {
        val startTime = System.currentTimeMillis()
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
        val endTime = System.currentTimeMillis()
        val durationMs = endTime - startTime
        log.info(
            "PERF TEST {}.{} lasted {}ms for {} requests (~{} requests per sec)",
            (this@AbstractJourneyPerfTests).javaClass.kotlin.simpleName,
            testName,
            durationMs,
            requestCount,
            requestCount * 1000 / durationMs
        )
    }
    
    @AfterEach
    fun tearDown() {
        runBlocking {
            userRepository.deleteAll()
            journeyRepository.deleteAll()
        }
    }
}