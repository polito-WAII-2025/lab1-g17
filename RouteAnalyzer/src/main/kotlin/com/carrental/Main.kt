package com.carrental

import com.carrental.parseCsv
import com.carrental.loadConfig

import com.carrental.computeMaxDistanceFromStart
import com.carrental.findMostFrequentedArea
import com.carrental.countWaypointsOutsideGeofence
import com.carrental.computeMostFrequentedAreaRadius


fun main() {
    val waypoints = parseCsv("waypoints.csv")
    val config = loadConfig("custom-parameters.yml")

    // Compute the maximum distance from the starting point using the Haversine formula
    val maxDistanceFromStart = computeMaxDistanceFromStart(waypoints, config.earthRadiusKm)
    // Determine the most frequented area based on the provided waypoints
    val mostFrequentedArea = findMostFrequentedArea(waypoints)
    // Count the number of waypoints that are outside the defined geofence area
    val waypointsOutsideGeofence =
        countWaypointsOutsideGeofence(
            waypoints,
            config.earthRadiusKm,
            config.geofenceCenterLatitude,
            config.geofenceCenterLongitude,
            config.geofenceRadiusKm
        )
    // Compute the radius of the most frequented area, using a default if not provided in the configuration
    val mostFrequentedAreaRadiusKm = computeMostFrequentedAreaRadius(
        config.mostFrequentedAreaRadiusKm,
        maxDistanceFromStart
    )
    println("Waypoints: $waypoints")
    println("Max Distance from Start: $maxDistanceFromStart km")
    println("Most Frequented Area: $mostFrequentedArea")
    println("Outside a specified geo-fence: $waypointsOutsideGeofence")
    println("Most Frequented Area Radius: $mostFrequentedAreaRadiusKm")
}