package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.data.model.CoordinatorCheckinSession
import com.example.ui.components.core.PrimaryButton
import com.example.ui.components.core.DangerButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CoordinatorCheckinPanel(
    session: CoordinatorCheckinSession,
    onEnd: () -> Unit
) {
    Dialog(onDismissRequest = onEnd) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.lg))
                .background(AppSurface)
                .border(2.dp, AppViolet, RoundedCornerShape(Radius.lg))
                .padding(Spacing.lg)
                .fillMaxWidth()
        ) {
            Column {
                Text("YOKLAMA DURUMU", style = MaterialTheme.typography.labelMedium, color = AppViolet)
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${session.safeMembers}", style = MaterialTheme.typography.headlineMedium, color = AppAcid)
                        Text("Güvende", style = MaterialTheme.typography.labelSmall, color = AppDim)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${session.helpMembers}", style = MaterialTheme.typography.headlineMedium, color = AppRed)
                        Text("Yardım Gerek", style = MaterialTheme.typography.labelSmall, color = AppRed)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${session.waitingMembers}", style = MaterialTheme.typography.headlineMedium, color = AppDim)
                        Text("Bekleniyor", style = MaterialTheme.typography.labelSmall, color = AppDim)
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Box(
                    modifier = Modifier.weight(1f, fill=false).fillMaxWidth().background(AppBg.copy(alpha=0.5f)).padding(Spacing.sm)
                ) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(session.logs) { log ->
                            Text(log, style = MaterialTheme.typography.bodySmall, color = if(log.contains("YARDIM")) AppRed else AppAcid)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                DangerButton(text = "Yoklamayı Sonlandır", onClick = onEnd, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun CheckinDialog(
    sourceLabel: String,
    onSafe: () -> Unit,
    onNeedHelp: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    // Shake animation state
    var isShaking by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        delay(100)
        isShaking = true
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                0f at 0
                20f at 50
                -20f at 100
                20f at 150
                -20f at 200
                10f at 250
                -10f at 300
                0f at 400
            }
        )
    }

    Dialog(
        onDismissRequest = { /* No-op */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                .clip(RoundedCornerShape(Radius.lg))
                .background(AppSurface)
                .border(2.dp, AppViolet, RoundedCornerShape(Radius.lg))
                .padding(Spacing.lg)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "YOKLAMA BAŞLATILDI",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppViolet,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
                Text(
                    text = "Koordinatör yoklama başlattı.",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppInk,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Kaynak: $sourceLabel\nBu adım ertelenemez.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppDim,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Spacing.xxl))
                
                PrimaryButton(
                    text = "✓ Güvendeyim",
                    onClick = onSafe,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                DangerButton(
                    text = "⚑ Yardım gerek",
                    onClick = onNeedHelp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
