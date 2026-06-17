package app.kotusenaryo.ui.screens

import android.os.Bundle
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kotusenaryo.data.model.CrisisMap
import app.kotusenaryo.data.model.MapPoint
import app.kotusenaryo.data.model.PointType
import app.kotusenaryo.ui.AppViewModel
import app.kotusenaryo.ui.theme.*
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import kotlin.math.roundToInt

@Composable
fun MapScreen(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var projectedPositions by remember { mutableStateOf<Map<String, android.graphics.PointF>>(emptyMap()) }

    // Dummy points, ideally from state
    val mockPoints = remember {
        listOf(
            MapPoint("p1", PointType.SHELTER, 41.015, 28.979, currentCount = 5, neededCount = 10, isEvent = true),
            MapPoint("p2", PointType.MEDICAL, 41.025, 28.989, currentCount = 2, neededCount = 5)
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { map ->
                        mapLibreMap = map
                        map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.015, 28.979), 12.0))
                        }
                        
                        map.addOnCameraMoveListener {
                            val projection = map.projection
                            val newPositions = mockPoints.associate { point ->
                                point.id to projection.toScreenLocation(LatLng(point.latitude, point.longitude))
                            }
                            projectedPositions = newPositions
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay points
        mockPoints.forEach { point ->
            val pos = projectedPositions[point.id]
            if (pos != null && pos.x >= 0 && pos.y >= 0) {
                Box(modifier = Modifier.offset { IntOffset(pos.x.roundToInt() - 24.dp.toPx().toInt(), pos.y.roundToInt() - 24.dp.toPx().toInt()) }) {
                    ZenginPointMarker(point) { viewModel.selectPoint(point.id) }
                }
            }
        }

        // Top UI
        Column(Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(Spacing.screenHorizontal)) {
            TopSummaryStrip(state.maps.first { it.id == state.currentMapId })
            Spacer(Modifier.height(Spacing.md))
            TypeFilterChips(
                activeFilter = state.activeFilter,
                onFilterSelect = { viewModel.toggleMapFilter(it) }
            )
        }

        // FAB center
        FloatingActionButton(
            onClick = {
                mapLibreMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.015, 28.979), 12.0))
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.screenHorizontal).padding(bottom = 80.dp),
            containerColor = AppSurface,
            contentColor = AppAcid
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "Konum")
        }
    }
}

@Composable
fun TopSummaryStrip(currentMap: CrisisMap) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(AppCard)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(currentMap.name, style = MaterialTheme.typography.titleMedium, color = AppText)
            Text(currentMap.description, style = MaterialTheme.typography.labelMedium, color = AppDim)
        }
        if (currentMap.isPrivate) {
            Icon(androidx.compose.material.icons.Icons.Filled.MyLocation, tint = AppAcid, contentDescription = null)
        }
    }
}

@Composable
fun TypeFilterChips(activeFilter: PointType?, onFilterSelect: (PointType) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        items(PointType.values()) { type ->
            val isSelected = activeFilter == type
            val bg = if (isSelected) AppAcid else AppCard
            val contentColor = if (isSelected) AppBg else AppText
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.pill))
                    .background(bg)
                    .clickable { onFilterSelect(type) }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            ) {
                Text("${type.emoji} ${type.label}", style = MaterialTheme.typography.labelLarge, color = contentColor)
            }
        }
    }
}

@Composable
fun ZenginPointMarker(point: MapPoint, onClick: () -> Unit) {
    val statusColor = when (point.type) {
        PointType.SHELTER -> AppAcid
        PointType.MEDICAL -> AppRed
        PointType.SUPPLY -> AppAmber
        PointType.HAZARD -> AppViolet
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (point.isEvent) 1.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        if (point.currentCount > 0 || point.neededCount > 0) {
            app.kotusenaryo.ui.components.core.CountBadge(
                current = point.currentCount,
                needed = point.neededCount,
                color = statusColor
            )
        } else {
             Text(point.type.label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.background(AppCard, RoundedCornerShape(4.dp)).padding(2.dp))
        }
        Box(contentAlignment = Alignment.Center) {
            if (point.isEvent) {
                Box(Modifier.size(48.dp).scale(pulseScale).clip(CircleShape).background(statusColor.copy(alpha = 0.3f)))
            }
            Box(Modifier.size(24.dp).clip(CircleShape).background(AppSurface).padding(4.dp)) {
                Box(Modifier.fillMaxSize().clip(CircleShape).background(statusColor))
            }
        }
    }
}

