package com.example.data.model

import kotlinx.serialization.Serializable

enum class PointStatus { FULL, NEED, CRITICAL, EVENT }

enum class PointType(val label: String, val emoji: String) {
    WATCHER("Gözcü", "👁️"),
    MEDICAL("Medikal", "⚕️"),
    GATHERING("Toplanma", "📍"),
    EVACUATION("Tahliye", "🏃"),
    EVENT("Olay", "⚠️")
}

@Serializable
data class Member(
    val initials: String,
    val colorHex: String
)

@Serializable
data class StationPoint(
    val id: String,
    val lat: Double,
    val lng: Double,
    val type: PointType,
    val title: String,
    val neededCount: Int,
    val currentCount: Int,
    val notes: List<String>,
    val roster: List<Member>,
    val isEvent: Boolean
) {
    val status: PointStatus
        get() = when {
            isEvent -> PointStatus.EVENT
            currentCount >= neededCount -> PointStatus.FULL
            currentCount > 0 -> PointStatus.NEED
            else -> PointStatus.CRITICAL
        }
}

enum class MapType { OFFICIAL, PARTY }
enum class Role { MEMBER, COORDINATOR }

@Serializable
data class CrisisMap(
    val id: String,
    val label: String,
    val networkLabel: String,
    val type: MapType,
    val role: Role,
    val badge: String?,
    val points: List<StationPoint>
)

@Serializable
data class Party(
    val key: String,
    val icon: String,
    val name: String,
    val members: Int,
    val online: Int,
    val role: Role?,
    val joined: Boolean,
    val tagLabel: String
)

enum class NotificationType {
    INFO, ALERT, CHECKIN, ROLE
}

@Serializable
data class AppNotification(
    val id: String,
    val title: String,
    val text: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean
)

@Serializable
data class CoordinatorCheckinSession(
    val isActive: Boolean = false,
    val totalMembers: Int = 0,
    val safeMembers: Int = 0,
    val helpMembers: Int = 0,
    val waitingMembers: Int = 0,
    val logs: List<String> = emptyList()
)

enum class UserStatus(val label: String) {
    AVAILABLE("MÜSAİT"),
    EN_ROUTE("YOLDA"),
    ON_DUTY("NÖBETTE")
}
