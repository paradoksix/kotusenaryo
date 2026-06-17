package app.kotusenaryo.data.model

data class CrisisMap(
    val id: String,
    val name: String,
    val description: String,
    val isPrivate: Boolean,
    val points: List<MapPoint> = emptyList()
)

data class MapPoint(
    val id: String,
    val type: PointType,
    val latitude: Double,
    val longitude: Double,
    val status: PointStatus = PointStatus.ACTIVE,
    val isEvent: Boolean = false,
    val currentCount: Int = 0,
    val neededCount: Int = 0
)

enum class PointType(val label: String, val emoji: String) {
    SHELTER("Sığınak", "🛡️"),
    MEDICAL("Medikal", "⚕️"),
    SUPPLY("Erzak", "📦"),
    HAZARD("Tehlike", "⚠️")
}

enum class PointStatus { ACTIVE, RESOLVED, URGENT }

data class UserProfile(
    val id: String,
    val username: String,
    val level: Int = 1,
    val points: Int = 0,
    val trustScore: Int = 100
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)
