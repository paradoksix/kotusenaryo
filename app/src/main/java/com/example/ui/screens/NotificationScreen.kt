package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.ui.components.core.AppCard
import com.example.ui.theme.*

@Composable
fun NotificationScreen(
    notifications: List<AppNotification>,
    onNotificationClick: (String) -> Unit
) {
    if (notifications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = AppDim, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(Spacing.md))
                Text("Henüz bir bildirim yok", style = MaterialTheme.typography.bodyLarge, color = AppDim)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.screenHorizontal),
        contentPadding = PaddingValues(vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item {
            Text("Son Etkinlikler", style = MaterialTheme.typography.titleLarge, color = AppInk)
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        items(notifications) { notif ->
            val icon = when (notif.type) {
                NotificationType.INFO -> Icons.Default.Info
                NotificationType.ALERT -> Icons.Default.Warning
                NotificationType.CHECKIN -> Icons.Default.CheckCircle
                NotificationType.ROLE -> Icons.Default.Notifications
            }
            val color = when (notif.type) {
                NotificationType.INFO -> AppBlue
                NotificationType.ALERT -> AppRed
                NotificationType.CHECKIN -> AppAcid
                NotificationType.ROLE -> AppAmber
            }
            
            val bgColor = if (notif.isRead) AppCard else AppCard.copy(alpha = 0.6f)
            val stripColor = if (!notif.isRead) color else Color.Transparent

            AppCard(
                onClick = { onNotificationClick(notif.id) },
                stripColor = stripColor,
                contentPadding = PaddingValues(Spacing.md)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                         Icon(icon, contentDescription = null, tint = color)
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(notif.title, style = MaterialTheme.typography.titleMedium, color = if (notif.isRead) AppDim else AppInk)
                            Text(notif.time, style = MaterialTheme.typography.labelSmall, color = AppDim)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notif.text, style = MaterialTheme.typography.bodyMedium, color = AppDim)
                    }
                }
            }
        }
    }
}
