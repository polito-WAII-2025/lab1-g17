package com.carrental

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
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

    // Compute the geofence result
    val geofenceResult = computeGeofenceResult(waypoints, config)

    // Save the result to a file
    saveGeofenceResultToFile(geofenceResult, "output.json")

    // Print output for debugging
    println("Results saved to output.json")
}





