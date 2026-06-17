package app.kotusenaryo.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.kotusenaryo.ui.theme.Radius
import app.kotusenaryo.ui.theme.AppBg

@Composable
fun StatusChip(status: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .background(color)
            .border(1.dp, AppBg, RoundedCornerShape(Radius.pill))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = AppBg
        )
    }
}

@Composable
fun CountBadge(current: Int, needed: Int, color: Color, modifier: Modifier = Modifier) {
    StatusChip(status = "$current/$needed", color = color, modifier = modifier)
}
