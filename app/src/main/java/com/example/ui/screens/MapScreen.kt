package com.example.ui.screens

import android.content.Context
import android.graphics.PointF
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.data.model.PointStatus
import com.example.data.model.StationPoint
import com.example.data.model.PointType
import com.example.ui.AppViewModel
import com.example.ui.theme.*
import com.example.ui.components.PointCardContent
import com.example.ui.components.core.AppCard
import com.example.ui.components.core.StatusChip
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Projection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val selectedPointId = state.selectedPointId
    val currentActiveMap = state.maps[state.currentMapId]
    val selectedPoint = currentActiveMap?.points?.find { it.id == selectedPointId }

    // Initialize MapLibre
    LaunchedEffect(Unit) {
        if (!MapLibre.hasInstance()) {
            MapLibre.getInstance(context.applicationContext)
        }
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    var maplibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var updateTrigger by remember { mutableLongStateOf(0L) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                if (!MapLibre.hasInstance()) {
                    MapLibre.getInstance(ctx.applicationContext)
                }
                MapView(ctx).apply {
                    getMapAsync { map ->
                        map.setStyle("asset://dark_style.json") { style ->
                            maplibreMap = map
                            val userLat = 41.0082
                            val userLng = 28.9784
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(userLat, userLng),
                                    12.0
                                )
                            )
                            map.addOnCameraMoveListener {
                                updateTrigger = System.currentTimeMillis()
                            }
                        }
                    }
                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { } // Handled via state snapshot below
        )

        // Overlay Summary Strip & Filters
        currentActiveMap?.points?.let { points ->
            val visiblePoints = points.filter { it.type in state.mapFilters }
            val criticals = visiblePoints.count { it.status == PointStatus.CRITICAL }
            val needs = visiblePoints.count { it.status == PointStatus.NEED || it.status == PointStatus.EVENT }
            val fulls = visiblePoints.count { it.status == PointStatus.FULL }

            Column(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (visiblePoints.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.pill))
                            .background(AppSurface.copy(alpha = 0.9f))
                            .border(1.dp, AppLine, RoundedCornerShape(Radius.pill))
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        if (criticals > 0) StatusChip("$criticals ACİL", AppRed)
                        if (needs > 0) StatusChip("$needs EKSİK", AppAmber)
                        if (fulls > 0) StatusChip("$fulls YETERLİ", AppAcid)
                    }
                } else if (points.isNotEmpty()) {
                    // Filtered everything out
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.pill))
                            .background(AppSurface.copy(alpha = 0.9f))
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    ) {
                        Text("Seçili filtrelerde nokta yok.", style = MaterialTheme.typography.labelSmall, color = AppDim)
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.padding(horizontal = Spacing.md)
                ) {
                    PointType.values().forEach { type ->
                        val isSelected = type in state.mapFilters
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.pill))
                                .background(if (isSelected) AppCard.copy(alpha = 0.9f) else AppBg.copy(alpha=0.7f))
                                .border(1.dp, if (isSelected) AppLine else Color.Transparent, RoundedCornerShape(Radius.pill))
                                .clickable { viewModel.toggleMapFilter(type) }
                                .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                        ) {
                            Text(
                                text = "${type.emoji} ${type.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) AppInk else AppDim
                            )
                        }
                    }
                }
            }
        }

        if (currentActiveMap?.points?.isEmpty() == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(AppBg.copy(alpha=0.8f)).padding(Spacing.lg).clip(RoundedCornerShape(Radius.lg))) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppDim, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text("Bu haritada henüz bir toplanma noktası yok.", style = MaterialTheme.typography.bodyMedium, color = AppDim)
                }
            }
        }

        // Draw overlay markers
        maplibreMap?.let { map ->
            currentActiveMap?.points?.filter { it.type in state.mapFilters }?.forEach { point ->
                PointMarker(
                    point = point,
                    projection = map.projection,
                    trigger = updateTrigger,
                    isSelected = point.id == selectedPointId,
                    onClick = { viewModel.selectPoint(point.id) }
                )
            }

            // User Location Mock Overlay
            UserLocationMarker(
                latLng = LatLng(41.0082, 28.9784),
                projection = map.projection,
                trigger = updateTrigger
            )
        }

        // FAB to re-center
        FloatingActionButton(
            onClick = {
                maplibreMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(41.0082, 28.9784),
                        12.0
                    )
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.lg),
            containerColor = AppAcid,
            contentColor = AppBg
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Ortala"
            )
        }

        if (selectedPoint != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectPoint(null) },
                sheetState = sheetState,
                containerColor = AppSurface,
                dragHandle = { BottomSheetDefaults.DragHandle(color = AppLine) }
            ) {
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(selectedPointId) {
                    isVisible = false
                    kotlinx.coroutines.delay(50)
                    isVisible = true
                }
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f))
                ) {
                    PointCardContent(
                        point = selectedPoint,
                        isJoined = selectedPoint.roster.any { it.initials == "BEN" },
                        onJoinClick = { viewModel.joinPoint(selectedPoint.id) },
                        onLeaveClick = { viewModel.leavePoint(selectedPoint.id) },
                        onDirectionsClick = { }
                    )
                }
            }
        }
    }

    // Lifecycle observer for MapView
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView?.onStart()
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_STOP -> mapView?.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun PointMarker(
    point: StationPoint,
    projection: Projection,
    trigger: Long,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (point.status) {
        PointStatus.EVENT -> AppBlue
        PointStatus.CRITICAL -> AppRed
        PointStatus.NEED -> AppAmber
        PointStatus.FULL -> AppAcid
    }

    val needsPulse = point.status == PointStatus.CRITICAL || point.status == PointStatus.EVENT
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (needsPulse) 1.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (needsPulse) 0.5f else 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val haptic = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "markerScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .layout { measurable, constraints ->
                trigger.hashCode() // Re-run layout on map camera move
                val screenPos = projection.toScreenLocation(LatLng(point.lat, point.lng))
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(
                        (screenPos.x - placeable.width / 2).toInt(),
                        (screenPos.y - placeable.height / 2).toInt()
                    )
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    ) {
        // Tag/Count badge
        if (!point.isEvent) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.pill))
                    .background(statusColor)
                    .border(1.dp, AppBg, RoundedCornerShape(Radius.pill))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${point.currentCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppBg
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        // Marker core
        Box(contentAlignment = Alignment.Center) {
            if (needsPulse) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = pulseAlpha
                        }
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppCard)
                    .border(if (isSelected) 3.dp else 2.dp, statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = point.type.emoji, style = MaterialTheme.typography.titleMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Name Label
        Box(
            modifier = Modifier
                .background(AppBg.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = point.title,
                style = MaterialTheme.typography.labelSmall,
                color = AppInk
            )
        }
    }
}

@Composable
fun UserLocationMarker(latLng: LatLng, projection: Projection, trigger: Long) {
    val infiniteTransition = rememberInfiniteTransition(label = "user_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.layout { measurable, constraints ->
            trigger.hashCode()
            val screenPos = projection.toScreenLocation(latLng)
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(
                    (screenPos.x - placeable.width / 2).toInt(),
                    (screenPos.y - placeable.height / 2).toInt()
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = pulseAlpha
                }
                .clip(CircleShape)
                .background(AppBlue)
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(AppBlue)
                .border(2.dp, Color.White, CircleShape)
        )
    }
}
