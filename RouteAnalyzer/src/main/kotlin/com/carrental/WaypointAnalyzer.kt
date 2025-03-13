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
 * Computes the maximum distance from the starting waypoint to any other waypoint in the list.
 * Uses the Haversine formula to calculate distances.
 * Returns a pair containing the waypoint with the maximum distance and the computed distance in kilometers.
 *
 * @param waypoints A list of waypoints to process.
 * @param earthRadiusKm The radius of the Earth in kilometers, used for Haversine distance calculation.
 * @return A Pair containing the waypoint that is the farthest from the starting point and the corresponding distance.
 * @throws IllegalArgumentException if the waypoints list is empty.
 */
fun computeMaxDistanceFromStart(waypoints: List<Waypoint>, earthRadiusKm: Double): Pair<Waypoint, Double> {
    val startPoint = waypoints.first()

    val distances = waypoints.map { waypoint ->
        val distance = haversine(earthRadiusKm, startPoint.latitude, startPoint.longitude, waypoint.latitude, waypoint.longitude)
        waypoint to distance
    }
    return distances.maxByOrNull { it.second }
        ?: throw IllegalArgumentException("Waypoints list cannot be empty")
}

/**
 * Determines the most frequently visited waypoint among the given waypoints.
 *
 * @param waypoints List of waypoints containing timestamp, latitude, and longitude.
 * @return A pair containing the most frequently visited waypoint and the number of times it was visited.
 */
fun findMostFrequentedArea(waypoints: List<Waypoint>): Pair<Waypoint, Int> {
    return waypoints
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.toPair() ?: throw IllegalArgumentException("Waypoints list cannot be empty")
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

/**
 * Filters waypoints that are outside the specified geofence.
 *
 * @param waypoints List of waypoints.
 * @param earthRadiusKm Earth's radius in kilometers.
 * @param centerLat Latitude of the geofence center.
 * @param centerLon Longitude of the geofence center.
 * @param radiusKm Radius of the geofence in kilometers.
 * @return List of waypoints outside the geofence.
 */
fun findWaypointsOutsideGeofence(
    waypoints: List<Waypoint>,
    earthRadiusKm: Double,
    centerLat: Double,
    centerLon: Double,
    radiusKm: Double
): List<Waypoint> {
    return waypoints.filter { haversine(earthRadiusKm, it.latitude, it.longitude, centerLat, centerLon) > radiusKm }
}

/**
 * Computes the central point of waypoints that are outside the geofence.
 * The center is calculated as the average latitude and longitude of all waypoints.
 *
 * @param waypointsOutside List of waypoints that are outside the geofence.
 * @return The computed central waypoint, or null if the list is empty.
 */
fun computeOutsideGeofenceCenter(waypointsOutside: List<Waypoint>): Waypoint? {
    if (waypointsOutside.isEmpty()) return null

    val avgLat = waypointsOutside.sumOf { it.latitude } / waypointsOutside.size
    val avgLon = waypointsOutside.sumOf { it.longitude } / waypointsOutside.size

    return Waypoint(0, avgLat, avgLon)
}
