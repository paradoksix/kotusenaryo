package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Role
import com.example.ui.AppViewModel
import com.example.ui.theme.*
import com.example.ui.components.core.*

@Composable
fun ProfileScreen(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.screenHorizontal),
        contentPadding = PaddingValues(top = Spacing.lg, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        // Profil Kartı
        item {
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AppBlue.copy(alpha = 0.2f))
                            .border(2.dp, AppBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", style = MaterialTheme.typography.headlineMedium, color = AppBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Gözcü #4417", style = MaterialTheme.typography.titleLarge, color = AppInk)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yıldız Mahallesi · Sektör C",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppDim
                        )
                    }
                    StatusChip(status = "GÜVENDE", color = AppAcid)
                }
            }
        }

        // Katkı İstatistikleri (Mock)
        item {
            Column {
                SectionHeader("Katkı İstatistikleri")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    StatCard(value = "12", label = "Nöbet", modifier = Modifier.weight(1f))
                    StatCard(value = "48h", label = "Saat", modifier = Modifier.weight(1f))
                    StatCard(value = "YÜKSEK", label = "Aktiflik", modifier = Modifier.weight(1.2f))
                }
            }
        }

        // Davet Kodu
        item {
            Column {
                SectionHeader("Davet Kodun")
                
                val dashColor = AppAmber.copy(alpha = 0.5f)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(Radius.lg))
                        .drawBehind {
                            val stroke = Stroke(
                                width = 4f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                            )
                            drawRoundRect(
                                color = dashColor,
                                style = stroke,
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                            )
                        }
                        .background(AppAmber.copy(alpha = 0.05f))
                        .clickable { viewModel.showNotification("Davet kodu kopyalandı") }
                        .padding(horizontal = Spacing.xl),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "GZ-4417",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppAmber,
                        letterSpacing = 8.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Paylaş",
                        tint = AppAmber
                    )
                }
            }
        }

        // Katıldığın Partiler
        item {
            Column {
                SectionHeader("Üye Olduğun Partiler")
                
                val joinedParties = state.parties.filter { it.joined }
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
                    ) {
                        joinedParties.forEachIndexed { index, party ->
                            val isCoord = party.role == Role.COORDINATOR
                            val roleColor = if (isCoord) AppAmber else AppBlue
                            val roleLabel = if (isCoord) "Koordinatör" else "Üye"
        
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = party.icon, style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.width(Spacing.md))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = party.name, style = MaterialTheme.typography.bodyLarge, color = AppInk)
                                }
                                StatTag(
                                    label = roleLabel,
                                    color = roleColor
                                )
                            }
                            if (index < joinedParties.size - 1) {
                                HorizontalDivider(color = AppLine, thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }

        // Son Durum Geçmişi
        item {
            Column {
                SectionHeader("Son Durum Geçmişin")
                
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(Spacing.lg)
                ) {
                    Column {
                        data class HistRow(val ic: androidx.compose.ui.graphics.vector.ImageVector, val col: Color, val txt: String, val time: String)
                        val items = listOf(
                            HistRow(Icons.Default.CheckCircle, AppAcid, "Güvende olarak işaretlendin.", "10 dk önce"),
                            HistRow(Icons.Default.Place, AppBlue, "Sultanahmet nöbetine katıldın.", "1 saat önce"),
                            HistRow(Icons.Default.Info, AppDim, "Sivil Ağ'a dahil oldun.", "1 gün önce")
                        )
                        items.forEachIndexed { index, histRow ->
                            HistoryTimelineItem(
                                icon = histRow.ic,
                                color = histRow.col,
                                text = histRow.txt,
                                time = histRow.time,
                                isLast = index == items.size - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier, contentPadding = PaddingValues(Spacing.md)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = AppAcid)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppDim)
        }
    }
}

@Composable
fun HistoryTimelineItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    text: String,
    time: String,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Timeline line and icon
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(2.dp, AppCard, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(AppLine)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(Spacing.md))
        
        // Content
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = time, style = MaterialTheme.typography.labelSmall, color = AppDim)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = AppInk)
        }
    }
}
