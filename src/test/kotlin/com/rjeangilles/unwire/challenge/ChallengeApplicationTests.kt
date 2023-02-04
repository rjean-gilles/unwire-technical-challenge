package com.rjeangilles.unwire.challenge

import com.rjeangilles.unwire.challenge.controller.JourneyController
import com.rjeangilles.unwire.challenge.model.Journey
import com.rjeangilles.unwire.challenge.model.UserId
import com.rjeangilles.unwire.challenge.service.JourneyService
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class ChallengeApplicationTests{
	@Autowired lateinit var journeyController: JourneyController
	@Autowired lateinit var journeyService: JourneyService

	@Test
	fun contextLoads() {
		assertThat(journeyController).isNotNull()
		assertThat(journeyService).isNotNull()
	}

}
