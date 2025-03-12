package com.carrental
import kotlin.math.*

/**
 * Computes the great-circle distance between two points using the Haversine formula.
 *
 * @param earthRadiusKm Radius of the Earth in kilometers.
 * @param lat1 Latitude of the first point in degrees.
 * @param lon1 Longitude of the first point in degrees.
 * @param lat2 Latitude of the second point in degrees.
 * @param lon2 Longitude of the second point in degrees.
 * @return Distance between the two points in kilometers.
 */
fun haversine(earthRadiusKm: Double, lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return 2 * earthRadiusKm * atan2(sqrt(a), sqrt(1 - a))
}

/**
 * Determines the most frequently visited location among waypoints.
 *
 * @param waypoints List of waypoints containing latitude and longitude.
 * @return The latitude and longitude of the most frequently visited location.
 */
fun findMostFrequentedArea(waypoints: List<Waypoint>): Pair<Double, Double> {
    return waypoints
        .groupingBy { it.latitude to it.longitude }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key ?: (0.0 to 0.0)
}

/**
 * Counts the number of waypoints that are outside a specified geofence.
 *
 * @param waypoints List of waypoints containing latitude and longitude.
 * @param earthRadiusKm Radius of the Earth in kilometers.
 * @param centerLat Latitude of the geofence center.
 * @param centerLon Longitude of the geofence center.
 * @param radiusKm Radius of the geofence in kilometers.
 * @return The number of waypoints outside the geofence.
 */
fun countWaypointsOutsideGeofence(waypoints: List<Waypoint>, earthRadiusKm:Double, centerLat: Double, centerLon: Double, radiusKm: Double): Int {
    return waypoints.count { haversine(earthRadiusKm, it.latitude, it.longitude, centerLat, centerLon) > radiusKm }
}

/**
 * Computes the radius of the most frequented area. If the radius is not provided,
 * it is computed based on the farthest distance traveled.
 *
 * @param mostFrequentedAreaRadiusKm Optional predefined radius of the most frequented area.
 * @param maxDistance The maximum distance traveled from the starting point.
 * @return The computed or provided most frequented area radius in kilometers.
 */
fun computeMostFrequentedAreaRadius(mostFrequentedAreaRadiusKm: Double?, maxDistance: Double): Double {
    return mostFrequentedAreaRadiusKm ?:
    if (maxDistance < 1.0) 0.1 else maxDistance * 0.1
}