package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.Party
import com.example.data.model.Role
import com.example.ui.AppViewModel
import com.example.ui.components.core.*
import com.example.ui.theme.*

@Composable
fun PartyScreen(viewModel: AppViewModel, showSnackbar: (String) -> Unit) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.screenHorizontal),
        contentPadding = PaddingValues(top = Spacing.lg, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item {
            Text(
                text = "Partiler",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Özel haritalar · party match",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        val joinedParties = state.parties.filter { it.joined }
        val otherParties = state.parties.filter { !it.joined }

        if (joinedParties.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.xs))
                SectionHeader("Katıldıkların")
            }
            items(joinedParties) { party ->
                PartyCard(
                    party = party,
                    onClick = {
                        viewModel.switchMap(party.key)
                        viewModel.switchTab(0)
                    }
                )
            }
        }

        if (otherParties.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
                SectionHeader("Keşfet")
            }
            items(otherParties) { party ->
                PartyCard(
                    party = party,
                    onClick = {
                        showSnackbar("Katılım isteği gönderildi (Demo)")
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.sm))
            val dashColor = AppLine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(Radius.lg))
                    .clickable { showSnackbar("Yeni parti kurma yakında (Demo)") }
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
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ YENİ ÖZEL HARİTA KUR",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppDim
                )
            }
        }
    }
}

@Composable
fun PartyCard(party: Party, onClick: () -> Unit) {
    AppCard(
        stripColor = if (party.joined) AppViolet else AppLine,
        onClick = onClick,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (party.joined) {
                        StatusChip(status = "KATILDIN", color = AppViolet)
                    } else {
                        StatTag(label = "DAVET KODU GEREKLİ", color = AppDim)
                    }
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    StatTag(label = "2 KRİTİK NOKTA", color = AppRed)
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = party.icon, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = party.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppInk
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "${party.members} üye · ${party.online} çevrimiçi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppDim
                )
                if (party.joined) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isCoordinator = party.role == Role.COORDINATOR
                        StatTag(
                            label = if (isCoordinator) "Koordinatör" else "Üye",
                            color = if (isCoordinator) AppAmber else AppBlue
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "zorunlu bildirim alırsın",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppDim
                        )
                    }
                }
            }

            Text(
                text = party.online.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = if (party.joined) AppAcid else AppDim
            )
        }
    }
}
