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
 * Computes the maximum distance between any two waypoints in the list.
 *
 * @param waypoints A list of waypoints containing latitude and longitude coordinates.
 * @param earthRadiusKm The radius of the Earth in kilometers.
 * @return The maximum distance found between any two waypoints. Returns 0.0 if the list has less than two waypoints.
 */
fun computeMaxDistance(waypoints: List<Waypoint>, earthRadiusKm: Double): Double {
    if (waypoints.size < 2) return 0.0

    return waypoints.maxOf { w1 ->
        waypoints.maxOf { w2 -> haversine(earthRadiusKm, w1.latitude, w1.longitude, w2.latitude, w2.longitude) }
    }
}

/**
 * Computes the radius for the most frequented area.
 *
 * This function determines an appropriate clustering radius for identifying the most frequented area.
 * If a custom radius is provided, it will be used; otherwise, a default radius is calculated based on
 * the maximum distance between waypoints.
 *
 * @param waypoints A list of waypoints containing latitude and longitude coordinates.
 * @param earthRadiusKm The radius of the Earth in kilometers.
 * @param mostFrequentedAreaRadiusKm (Optional) A predefined radius for clustering waypoints.
 *        If null, the function will compute a default radius.
 * @return The computed radius, rounded to one decimal place.
 *         - If the max distance between waypoints is less than 1 km, the default radius is 0.1 km.
 *         - Otherwise, the radius is 10% of the maximum distance.
 */
fun computeMostFrequentedAreaRadius(
    waypoints: List<Waypoint>,
    earthRadiusKm: Double,
    mostFrequentedAreaRadiusKm: Double?= null
): Double {
    val maxDistance = computeMaxDistance(waypoints, earthRadiusKm)
    val radius = mostFrequentedAreaRadiusKm ?: if (maxDistance < 1.0) 0.1 else maxDistance * 0.1
    return round(radius * 10) / 10 // Rounds to one decimal place
}

/**
 * Finds the most frequented area based on a clustering approach.
 *
 * This function iterates through all waypoints, treating each as a potential center,
 * and counts how many waypoints fall within a defined radius. The waypoint with the highest count is selected.
 *
 * @param waypoints A list of waypoints containing latitude and longitude coordinates.
 * @param earthRadiusKm The radius of the Earth in kilometers.
 * @param mostFrequentedAreaRadiusKm (Optional) A predefined radius for clustering waypoints.
 *        If not provided, the function will compute an appropriate radius based on the maximum distance between waypoints.
 * @return A pair consisting of:
 *         - The waypoint that represents the most frequented area.
 *         - The number of waypoints within the defined radius of that central waypoint.
 * @throws IllegalArgumentException If the waypoint list is empty.
 */
fun findMostFrequentedArea(
    waypoints: List<Waypoint>,
    earthRadiusKm: Double,
    mostFrequentedAreaRadiusKm: Double? = null
): Pair<Waypoint, Int> {
    if (waypoints.isEmpty()) throw IllegalArgumentException("Waypoints list cannot be empty")
    val radiusKm = computeMostFrequentedAreaRadius(waypoints, earthRadiusKm, mostFrequentedAreaRadiusKm)

    return waypoints
        .map { center ->
            val count = waypoints.count { other ->
                haversine(earthRadiusKm, center.latitude, center.longitude, other.latitude, other.longitude) <= radiusKm
            }
            center to count
        }
        .maxByOrNull { it.second } ?: (waypoints.first() to 1)

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