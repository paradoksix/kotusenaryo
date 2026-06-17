package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PointStatus
import com.example.data.model.StationPoint
import com.example.ui.components.core.*
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PointCardContent(
    point: StationPoint,
    isJoined: Boolean,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    val statusColor = when (point.status) {
        PointStatus.EVENT -> Blue
        PointStatus.CRITICAL -> Red
        PointStatus.NEED -> Amber
        PointStatus.FULL -> AcidGreen
    }

    val statusLabel = when (point.status) {
        PointStatus.EVENT -> "ETKİNLİK"
        PointStatus.CRITICAL -> "ACİL"
        PointStatus.NEED -> "EKSİK"
        PointStatus.FULL -> "YETERLİ"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, statusColor, RoundedCornerShape(12.dp))
                        .background(CardColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = point.type.emoji, style = MaterialTheme.typography.headlineMedium)
                }
                Spacer(modifier = Modifier.width(Spacing.md))
                Column {
                    Text(
                        text = point.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = point.type.label.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = AppDim
                    )
                }
            }
            
            StatusChip(
                status = statusLabel,
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Need Indicator
        if (point.isEvent) {
            Text(
                text = "Açık etkinlik · katılım serbest",
                style = MaterialTheme.typography.bodyLarge,
                color = AppDim
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${point.currentCount}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppInk
                )
                Text(
                    text = " / ${point.neededCount} kişi",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppDim
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            SegmentedProgress(
                current = point.currentCount,
                needed = point.neededCount,
                color = statusColor
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            val shortage = point.neededCount - point.currentCount
            Text(
                text = if (shortage > 0) "$shortage kişi gerekli" else "tam kadro",
                style = MaterialTheme.typography.labelMedium,
                color = AppDim
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Notes
        if (point.notes.isNotEmpty()) {
            AppCard(
                stripColor = AppAmber,
                contentPadding = PaddingValues(Spacing.md)
            ) {
                SectionHeader("Nokta Notları")
                point.notes.forEach { note ->
                    Text(
                        text = "• $note",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppInk
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        // Team roster
        SectionHeader("Nöbetteki Ekip")
        if (point.roster.isEmpty()) {
            Text(
                text = "Bu nokta boş.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppRed
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                point.roster.forEach { member ->
                    val memberColor = try {
                        Color(android.graphics.Color.parseColor(member.colorHex))
                    } catch (e: Exception) {
                        Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(memberColor.copy(alpha = 0.2f))
                            .border(1.dp, memberColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.initials,
                            style = MaterialTheme.typography.labelMedium,
                            color = memberColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Recent Activity Timeline (Mock)
        SectionHeader("Son Hareketler")
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            ActivityRow("10 dk önce", "AHMET katıldı")
            ActivityRow("45 dk önce", "MERT ayrıldı")
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Actions
        Row(modifier = Modifier.fillMaxWidth()) {
            if (point.isEvent) {
                PrimaryButton(
                    text = "Etkinliğe Yönlen",
                    onClick = onJoinClick,
                    modifier = Modifier.weight(1f)
                )
            } else if (isJoined) {
                DangerButton(
                    text = "Nöbetten Ayrıl",
                    onClick = onLeaveClick,
                    modifier = Modifier.weight(1f)
                )
            } else if (point.status == PointStatus.FULL) {
                GhostButton(
                    text = "Nokta Dolu — Yedek Ol",
                    onClick = onJoinClick,
                    modifier = Modifier.weight(1f)
                )
            } else {
                PrimaryButton(
                    text = "Nöbete Katıl",
                    onClick = onJoinClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            GhostButton(
                text = "Yol Tarifi",
                onClick = onDirectionsClick
            )
        }
        
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun ActivityRow(time: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LedDot(color = AppBlue, pulsing = false)
        Spacer(modifier = Modifier.width(Spacing.md))
        Text(text = time, style = MaterialTheme.typography.labelSmall, color = AppDim, modifier = Modifier.width(70.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = AppInk)
    }
}
