package journeyapi

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

class GetJourneysByUserIdSimulation : Simulation() {

    private val userId = 1

    val protocol: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/")
    val scn: ScenarioBuilder = scenario("GetJourneysByUserId")
        .exec{ session -> session.set("userId", userId) }
        .repeat(10).on(
            exec(
                http("Get Journeys")
                    .get("user/#{userId}/journeys")
                    .asJson()
                    .check(status().shouldBe(200))
            )
        )

  init {
    setUp(
      scn.injectClosed(constantConcurrentUsers(20).during(10))
        .protocols(protocol)
    )
    .maxDuration(1800)
    .assertions(global().responseTime().max().lt(20000), global().successfulRequests().percent().gt(95.0))
  }
}