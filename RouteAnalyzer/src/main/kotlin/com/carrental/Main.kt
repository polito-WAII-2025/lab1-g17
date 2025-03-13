package com.carrental

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

// Data class to store computed results
data class GeofenceResult(
    val waypointsOutsideGeofence: Int,
    val mostFrequentedArea: Waypoint,
    val mostFrequentedAreaRadiusKm: Double,
    val maxDistanceFromStart: Double
)

fun main() {
    val waypoints = parseCsv("waypoints.csv")
    val config = loadConfig("custom-parameters.yml")

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
    val waypointsOutsideCount = waypointsOutside.size

    val geofenceResult = GeofenceResult(
        waypointsOutsideGeofence = waypointsOutsideCount,
        mostFrequentedArea = mostFrequentedArea,
        mostFrequentedAreaRadiusKm = mostFrequentedAreaRadiusKm,
        maxDistanceFromStart = maxDistanceFromStart
    )
    /*
    println("Max Distance from Start: $maxDistanceFromStart km")
    println("Most Frequented Area: $mostFrequentedArea")
    println("Outside a specified geo-fence: $waypointsOutsideGeofence")
    println("Most Frequented Area Radius: $mostFrequentedAreaRadiusKm")
     */


    // Convert result to pretty-printed JSON and save to file
    val jsonMapper = jacksonObjectMapper().writerWithDefaultPrettyPrinter()
    val jsonString = jsonMapper.writeValueAsString(geofenceResult)

    File("output.json").writeText(jsonString)

// Print output for debugging
    println("Results saved to output.json")
    println(jsonString)
}

/*
    println("Waypoints: $waypoints")
    println("Max Distance from Start: $maxDistanceFromStart km. Farthest Waypoint: $farthestWaypoint")
    println("Most Frequented Area: $mostFrequentedArea. Most Frequented Entries Count: $mostFrequentedEntriesCount. Most Frequented Area Radius: $mostFrequentedAreaRadiusKm")
    println("Points outside a specified geo-fence: $waypointsOutside. Center of Points: $waypointsOutsideCenterPoint. Number of points: $waypointsOutsideCount")


*/

