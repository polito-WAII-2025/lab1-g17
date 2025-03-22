package com.carrental

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





