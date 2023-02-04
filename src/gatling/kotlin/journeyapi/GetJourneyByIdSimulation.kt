package journeyapi

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

class GetJourneyByIdSimulation : Simulation() {

    private val userId = 1

    val protocol: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/")
    val scn: ScenarioBuilder = scenario("GetJourneyById")
        .exec{ session -> session.set("userId", userId) }
        .exec(
            http("Create new journey")
                .post("user/#{userId}/journeys")
                .asJson()
                .body(StringBody("""
                  |{
                  |  "startAddress": "1620 S Mission St",
                  |  "endAddress": "1880 Willamette Falls Dr"
                  |}""".trimMargin())
                )
                .check(status().shouldBe(200))
                .check(jsonPath("\$.id").saveAs("journeyId"))
        )
        .repeat(50).on(
            exec(
                http("Get Journey")
                    .get("journey/#{journeyId}")
                    .asJson()
                    .check(status().shouldBe(200))
            )
        )
        .exec(
            http("Delete new journey")
                .delete("journey/#{journeyId}")
                .asJson()
                .check(status().shouldBe(200))
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