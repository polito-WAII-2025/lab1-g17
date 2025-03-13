package com.carrental

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.nio.charset.StandardCharsets
import org.yaml.snakeyaml.Yaml
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.carrental.haversine
import com.carrental.findMostFrequentedArea
import com.carrental.computeMostFrequentedAreaRadius
import com.carrental.findWaypointsOutsideGeofence
import com.carrental.computeOutsideGeofenceCenter

data class Config(
    val earthRadiusKm: Double,
    val geofenceCenterLatitude: Double,
    val geofenceCenterLongitude: Double,
    val geofenceRadiusKm: Double,
    val mostFrequentedAreaRadiusKm: Double? = null)

fun loadConfig(filePath: String): Config {
    val yaml = Yaml()
    val file = File(filePath).readText()
    val map: Map<String, Any> = yaml.load(file)

    return Config(
        earthRadiusKm = map["earthRadiusKm"] as Double,
        geofenceCenterLatitude = map["geofenceCenterLatitude"] as Double,
        geofenceCenterLongitude = map["geofenceCenterLongitude"] as Double,
        geofenceRadiusKm = map["geofenceRadiusKm"] as Double,
        mostFrequentedAreaRadiusKm = map["mostFrequentedAreaRadiusKm"] as? Double
    )
}

data class Waypoint(val timestamp: Long, val latitude: Double, val longitude: Double)

fun parseCsv(filePath: String): List<Waypoint> {
    val file = File(filePath)
    val csvFormat = CSVFormat.Builder.create()
        .setDelimiter(';')
        .setHeader()
        .setSkipHeaderRecord(true)
        .build()
    val parsedFile = CSVParser(
        file.bufferedReader(StandardCharsets.UTF_8),
        csvFormat
    )

    return parsedFile.records.map {
        Waypoint(
            timestamp = it[0].toDouble().toLong(),
            latitude = it[1].toDouble(),
            longitude = it[2].toDouble()
        )
    }
}

// Data class to store computed results
data class GeofenceResult(
    val waypointsOutsideGeofence: Int,
    val mostFrequentedArea: Pair<Double, Double>,
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
        config.geofenceRadiusKm)
    val waypointsOutsideCenterPoint = computeOutsideGeofenceCenter(waypointsOutside)
    val waypointsOutsideCount = waypointsOutside.size


    val geofenceResult = GeofenceResult(
        waypointsOutsideGeofence = waypointsOutsideGeofence,
        mostFrequentedArea = mostFrequentedArea,
        mostFrequentedAreaRadiusKm = mostFrequentedAreaRadiusKm,
        maxDistanceFromStart = maxDistanceFromStart
    )
/*
    // Convert result to JSON and save to a file
    val jsonMapper = jacksonObjectMapper()
    val jsonString = jsonMapper.writeValueAsString(geofenceResult)
    File("output.json").writeText(jsonString)
    // Print output for debugging
    println("Results saved to output.json")
    println(jsonString)
*/
    // Convert result to pretty-printed JSON and save to file
    val jsonMapper = jacksonObjectMapper().writerWithDefaultPrettyPrinter()
    val jsonString = jsonMapper.writeValueAsString(geofenceResult)
    File("output.json").writeText(jsonString)

    // Print output for debugging
    println("Results saved to output.json")
    println(jsonString)
/*
    val geofenceResult = GeofenceResult(
        waypointsOutsideGeofence = waypointsOutsideGeofence,
        mostFrequentedArea = mostFrequentedArea,
        mostFrequentedAreaRadiusKm = mostFrequentedAreaRadiusKm,
        maxDistanceFromStart = maxDistanceFromStart
    )

    // Convert result to JSON and save to a file
    val jsonMapper = jacksonObjectMapper()
    val jsonString = jsonMapper.writeValueAsString(geofenceResult)
    File("geofence_result.json").writeText(jsonString)

    // Print output for debugging
    println("Results saved to geofence_result.json")
    println(jsonString)
/*
    println("Waypoints: $waypoints")
    println("Max Distance from Start: $maxDistanceFromStart km. Farthest Waypoint: $farthestWaypoint")
    println("Most Frequented Area: $mostFrequentedArea. Most Frequented Entries Count: $mostFrequentedEntriesCount. Most Frequented Area Radius: $mostFrequentedAreaRadiusKm")
    println("Points outside a specified geo-fence: $waypointsOutside. Center of Points: $waypointsOutsideCenterPoint. Number of points: $waypointsOutsideCount")

    println("Max Distance from Start: $maxDistanceFromStart km")
    println("Most Frequented Area: $mostFrequentedArea")
    println("Outside a specified geo-fence: $waypointsOutsideGeofence")
    println("Most Frequented Area Radius: $mostFrequentedAreaRadiusKm")
*/

}