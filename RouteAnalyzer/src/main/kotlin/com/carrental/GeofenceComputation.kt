package com.carrental

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

// Data classes for structured output
data class MaxDistanceFromStart(
    val waypoint: Waypoint,
    val distanceKm: Double
)

data class MostFrequentedArea(
    val centralWaypoint: Waypoint,
    val areaRadiusKm: Double,
    val entriesCount: Int
)

data class WaypointsOutsideGeofence(
    val centralWaypoint: Waypoint?,
    val areaRadiusKm: Double,
    val Count: Int,
    val waypoints: List<Waypoint>,
)

data class GeofenceResult(
    val maxDistanceFromStart: MaxDistanceFromStart,
    val mostFrequentedArea: MostFrequentedArea,
    val waypointsOutsideGeofence: WaypointsOutsideGeofence,
)

fun computeGeofenceResult(waypoints: List<Waypoint>, config: Config): GeofenceResult {
    // Compute the maximum distance from the starting point using the Haversine formula
    val (farthestWaypoint, maxDistanceFromStart) = computeMaxDistanceFromStart(waypoints, config.earthRadiusKm)

    // Determine the most frequented area and entries count based on the provided waypoints
    val (mostFrequentedArea, mostFrequentedEntriesCount) = findMostFrequentedArea(waypoints)

    // Compute the radius of the most frequented area, using a default if not provided in the configuration
    val mostFrequentedAreaRadiusKm = computeMostFrequentedAreaRadius(
        config.mostFrequentedAreaRadiusKm,
        maxDistanceFromStart
    )

    // Compute waypoints that are outside the defined geofence area
    val waypointsOutside = findWaypointsOutsideGeofence(
        waypoints,
        config.earthRadiusKm,
        config.geofenceCenterLatitude,
        config.geofenceCenterLongitude,
        config.geofenceRadiusKm
    )
    val waypointsOutsideCenterPoint = computeOutsideGeofenceCenter(waypointsOutside)
    val Count = waypointsOutside.size

    // Construct and return output object
    return GeofenceResult(
        maxDistanceFromStart = MaxDistanceFromStart(
            waypoint = farthestWaypoint,
            distanceKm = maxDistanceFromStart
        ),
        mostFrequentedArea = MostFrequentedArea(
            centralWaypoint = mostFrequentedArea,
            areaRadiusKm = mostFrequentedAreaRadiusKm,
            entriesCount = mostFrequentedEntriesCount
        ),
        waypointsOutsideGeofence = WaypointsOutsideGeofence(
            centralWaypoint = waypointsOutsideCenterPoint,
            waypoints = waypointsOutside,
            Count = Count,
            areaRadiusKm = config.geofenceRadiusKm
        )
    )
}

fun saveGeofenceResultToFile(geofenceResult: GeofenceResult, fileName: String) {
    // Convert result to pretty-printed JSON and save to file
    val jsonMapper = jacksonObjectMapper().writerWithDefaultPrettyPrinter()
    val jsonString = jsonMapper.writeValueAsString(geofenceResult)
    File(fileName).writeText(jsonString)
}
