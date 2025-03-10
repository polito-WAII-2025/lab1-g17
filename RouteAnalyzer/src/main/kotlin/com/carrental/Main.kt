package com.carrental

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.nio.charset.StandardCharsets

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


fun main() {
    val waypoints = parseCsv("waypoints.csv")
    println("Waypoints: $waypoints")
}