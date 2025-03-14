package com.carrental

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Data class representing configuration parameters loaded from a YAML file.
 * @param earthRadiusKm Earth's radius in kilometers.
 * @param geofenceCenterLatitude Latitude of the geofence center.
 * @param geofenceCenterLongitude Longitude of the geofence center.
 * @param geofenceRadiusKm Radius of the geofence area in kilometers.
 * @param mostFrequentedAreaRadiusKm (Optional) Radius of the most frequented area in kilometers.
 */
data class Config(
    val earthRadiusKm: Double,
    val geofenceCenterLatitude: Double,
    val geofenceCenterLongitude: Double,
    val geofenceRadiusKm: Double,
    val mostFrequentedAreaRadiusKm: Double? = null
)

/**
 * Data class representing a waypoint with a timestamp, latitude, and longitude.
 * @param timestamp Time in milliseconds when the waypoint was recorded.
 * @param latitude Latitude coordinate of the waypoint.
 * @param longitude Longitude coordinate of the waypoint.
 */
data class Waypoint(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
)

/**
 * Loads configuration parameters from a YAML file.
 * @param filePath Path to the YAML configuration file.
 * @return Config object containing parsed parameters.
 */
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

/**
 * Parses a CSV file containing waypoint data.
 * @param filePath Path to the CSV file.
 * @return List of Waypoint objects parsed from the CSV file.
 */
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