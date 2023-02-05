package com.rjeangilles.unwire.challenge.model

import java.time.Instant
import java.util.Collections.emptyList

enum class TravelMode {
    WALK, BICYCLE, CAR, BUS, SUBWAY, TRAIN, TRAM, RAIL
}

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)

typealias LocationId = Long

data class Location(
    val id: LocationId?,
    val geoPoint: GeoPoint
)

data class Step(
    val travelMode: TravelMode,
    val startDate: Instant,
    val startLocation: Location,
    val endDate: Instant,
    val endLocation: Location
)

typealias JourneyId = Long

data class Journey(
    val id: JourneyId? = null,
    val startAddress: String? = null,
    val startLocation: Location? = null,
    val endAddress: String? = null,
    val endLocation: Location? = null,
    val steps: List<Step> = emptyList(),
)