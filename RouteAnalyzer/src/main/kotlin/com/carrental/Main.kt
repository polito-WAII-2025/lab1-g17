package com.carrental

import com.carrental.parseCsv
import com.carrental.loadConfig

import com.carrental.computeMaxDistanceFromStart
import com.carrental.findMostFrequentedArea
import com.carrental.computeMostFrequentedAreaRadius
import com.carrental.findWaypointsOutsideGeofence
import com.carrental.computeOutsideGeofenceCenter


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
        config.geofenceRadiusKm)
    val waypointsOutsideCenterPoint = computeOutsideGeofenceCenter(waypointsOutside)
    val waypointsOutsideCount = waypointsOutside.size


    println("Waypoints: $waypoints")
    println("Max Distance from Start: $maxDistanceFromStart km. Farthest Waypoint: $farthestWaypoint")
    println("Most Frequented Area: $mostFrequentedArea. Most Frequented Entries Count: $mostFrequentedEntriesCount. Most Frequented Area Radius: $mostFrequentedAreaRadiusKm")
    println("Points outside a specified geo-fence: $waypointsOutside. Center of Points: $waypointsOutsideCenterPoint. Number of points: $waypointsOutsideCount")

}