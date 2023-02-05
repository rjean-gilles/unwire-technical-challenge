package com.rjeangilles.unwire.challenge

import com.rjeangilles.unwire.challenge.model.*
import com.rjeangilles.unwire.challenge.service.JourneyService
import com.rjeangilles.unwire.challenge.service.UserService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

@SpringBootTest
@AutoConfigureWebTestClient
class WebLayerTests {
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var journeyService: JourneyService
    @Autowired private lateinit var webTestClient: WebTestClient

    private var user1Id: UserId? = null
    private var user2Id: UserId? = null
    private var journey1Id: JourneyId? = null
    private var journey2Id: JourneyId? = null
    private var journey3Id: JourneyId? = null

    @BeforeEach
    fun setUp() {
        //journeyService.createRandomTestJourney(1)

        //region fixture init
        runBlocking {
            user1Id = userService.create(
                User(
                    name = "Foo"
                )
            ).id
            user2Id = userService.create(
                User(
                    name = "Bar"
                )
            ).id

            journey1Id = journeyService.create(
                user1Id!!,
                Journey(
                    startAddress = "1959 NE Pacific St",
                    endAddress = "2100 W Genesee Turnpike",
                    steps = listOf(
                        Step(
                            TravelMode.WALK,
                            Instant.parse("2023-01-01T12:49:27Z"),
                            Location(
                                103,
                                GeoPoint(43.547165, -106.626785)
                            ),
                            Instant.parse("2023-01-01T12:55:05Z"),
                            Location(
                                215,
                                GeoPoint(43.991442, -97.837722)
                            )
                        ),
                        Step(
                            TravelMode.SUBWAY,
                            Instant.parse("2023-01-01T12:56:33Z"),
                            Location(
                                103,
                                GeoPoint(43.551234, -106.633333)
                            ),
                            Instant.parse("2023-01-01T13:10:12Z"),
                            Location(
                                215,
                                GeoPoint(43.998163, -97.836534)
                            )
                        )
                    )
                )
            ).id

            journey2Id = journeyService.create(
                user1Id!!,
                Journey(
                    startAddress = "1620 S Mission St",
                    endAddress = "1880 Willamette Falls Dr",
                    steps = listOf(
                        Step(
                            TravelMode.BICYCLE,
                            Instant.parse("2023-01-01T15:15:33Z"),
                            Location(
                                203,
                                GeoPoint(43.551234, -106.633333)
                            ),
                            Instant.parse("2023-01-01T13:10:12Z"),
                            Location(
                                335,
                                GeoPoint(43.998163, -97.836534)
                            )
                        )
                    )
                )
            ).id

            journey3Id = journeyService.create(
                user2Id!!,
                Journey(
                    startLocation = Location(
                        null,
                        GeoPoint(43.547165, -106.626785)
                    ),
                    endAddress = "1880 Willamette Falls Dr",
                    steps = listOf(
                        Step(
                            TravelMode.WALK,
                            Instant.parse("2023-01-02T12:49:27Z"),
                            Location(
                                null,
                                GeoPoint(43.547165, -106.626785)
                            ),
                            Instant.parse("2023-01-02T12:55:05Z"),
                            Location(
                                null,
                                GeoPoint(43.95, -106.41)
                            )
                        ),
                        Step(
                            TravelMode.RAIL,
                            Instant.parse("2023-01-02T12:56:33Z"),
                            Location(
                                71,
                                GeoPoint(43.856, -106.512)
                            ),
                            Instant.parse("2023-01-02T13:10:12Z"),
                            Location(
                                215,
                                GeoPoint(43.965, -106.432)
                            )
                        ),
                        Step(
                            TravelMode.WALK,
                            Instant.parse("2023-01-02T13:15:35Z"),
                            Location(
                                null,
                                GeoPoint(43.843, -106.298)
                            ),
                            Instant.parse("2023-01-02T13:33:56Z"),
                            Location(
                                null,
                                GeoPoint(43.754, -106.3652)
                            )
                        ),
                    )
                )
            ).id
        }
        //endregion
    }

    @AfterEach
    fun tearDown() {
        runBlocking {
            journeyService.deleteAll()
        }
    }

    @Test
    fun testGetAllJourneysByUserId() {
        webTestClient.get().uri("/user/${user1Id}/journeys")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("\$").isArray()
            .jsonPath("\$.length()").isEqualTo(2)
            .jsonPath("\$[0].startAddress").isEqualTo("1959 NE Pacific St")
            .jsonPath("\$[0].endAddress").isEqualTo("2100 W Genesee Turnpike")
            .jsonPath("\$[1].startAddress").isEqualTo("1620 S Mission St")
            .jsonPath("\$[1].endAddress").isEqualTo("1880 Willamette Falls Dr")

    }

    @Test
    fun testGetJourneyById() {
        webTestClient.get().uri("/journey/${journey2Id}")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("\$").isMap()
            .jsonPath("\$.startAddress").isEqualTo("1620 S Mission St")
            .jsonPath("\$.endAddress").isEqualTo("1880 Willamette Falls Dr")
            .jsonPath("\$.steps").isArray()
            .jsonPath("\$.steps.length()").isEqualTo(1)
            .jsonPath("\$.steps[0].travelMode").isEqualTo("BICYCLE")

    }
}