package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.data.model.CrisisMap
import com.example.data.model.MapType
import com.example.data.model.UserStatus
import com.example.ui.screens.MapScreen
import com.example.ui.theme.*

@Composable
fun OfficialBanner(
    text: String,
    onGo: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF13202A))
            .border(1.dp, AppBlue)
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Resmi Harita · Bilgilendirme",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppBlue.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onGo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppBlue,
                    contentColor = AppBg
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Git", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Kapat",
                    tint = AppBlue
                )
            }
        }
    }
}

@Composable
fun AppScaffold(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.activeNotification) {
        state.activeNotification?.let { notif ->
            snackbarHostState.showSnackbar(notif)
            viewModel.clearNotification()
        }
    }

    if (state.activeCheckin != null) {
        com.example.ui.components.CheckinDialog(
            sourceLabel = state.activeCheckin!!,
            onSafe = { viewModel.respondCheckin(true) },
            onNeedHelp = { viewModel.respondCheckin(false) }
        )
    }
    
    val session = state.coordinatorSession
    if (session != null) {
        com.example.ui.components.CoordinatorCheckinPanel(
            session = session,
            onEnd = { viewModel.stopCoordinatorSession() }
        )
    }

    val activeMap = state.maps[state.currentMapId ?: ""]

    AnimatedContent(
        targetState = state.isOnboarded,
        transitionSpec = {
            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "onboard_transition"
    ) { isOnboarded ->
        if (!isOnboarded) {
            com.example.ui.screens.OnboardingScreen(
                isVerifying = state.isVerifyingCode,
                error = state.verificationError,
                onVerifyClick = { code -> viewModel.verifyCode(code) }
            )
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Column(modifier = Modifier.background(AppSurface).statusBarsPadding()) {
                        AnimatedVisibility(
                            visible = state.activeBanner != null,
                            enter = slideInVertically(initialOffsetY = { -it }),
                            exit = slideOutVertically(targetOffsetY = { -it })
                        ) {
                            if (state.activeBanner != null) {
                                OfficialBanner(
                                    text = state.activeBanner!!,
                                    onGo = { viewModel.actOnBanner() },
                                    onDismiss = { viewModel.dismissBanner() }
                                )
                            }
                        }
                        TopBarContent(
                            activeMap = activeMap,
                            userStatus = state.userStatus,
                            onStatusClick = { viewModel.cycleStatus() }
                        )
                        MapChipStrip(
                            maps = state.maps.values.toList(),
                            currentMapId = state.currentMapId,
                            onMapClick = { viewModel.switchMap(it) }
                        )
                    }
                },
                bottomBar = {
                    AppBottomNavigation(
                        currentTab = state.currentTab,
                        activeMap = activeMap,
                        onTabSelect = { viewModel.switchTab(it) },
                        onTriggerCoordAction = { viewModel.triggerCoordAction() }
                    )
                },
                containerColor = AppBg,
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { padding ->
                Box(modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()) {
                    AnimatedContent(
                        targetState = state.currentTab,
                        transitionSpec = {
            (slideInHorizontally(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
                initialOffsetX = { if (targetState > initialState) it else -it }
            ) + fadeIn(animationSpec = tween(200))).togetherWith(
                slideOutHorizontally(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
                    targetOffsetX = { if (targetState > initialState) -it else it }
                ) + fadeOut(animationSpec = tween(200))
            )
        },
                        label = "tab_transition"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> MapScreen(viewModel = viewModel)
                            1 -> com.example.ui.screens.PartyScreen(
                                viewModel = viewModel,
                                showSnackbar = { msg -> viewModel.showNotification(msg) }
                            )
                            2 -> com.example.ui.screens.NotificationScreen(
                                notifications = state.notifications,
                                onNotificationClick = { id -> viewModel.markNotificationRead(id) }
                            )
                            3 -> com.example.ui.screens.ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarContent(
    activeMap: CrisisMap?,
    userStatus: UserStatus,
    onStatusClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenHorizontal, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = buildAnnotatedString {
                    append("KÖTÜ SENARYO")
                    withStyle(style = SpanStyle(color = AppRed)) {
                        append(".")
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                color = AppInk
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            if (activeMap != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = if (activeMap.type == MapType.OFFICIAL) AppBlue else AppAmber
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = activeMap.networkLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.lg))
                .background(AppCard)
                .clickable { onStatusClick() }
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor = when (userStatus) {
                    UserStatus.AVAILABLE -> AppAcid
                    UserStatus.EN_ROUTE -> AppAmber
                    UserStatus.ON_DUTY -> AppRed
                }
                
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .alpha(alpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = userStatus.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = AppInk
                )
            }
        }
    }
}

@Composable
fun MapChipStrip(
    maps: List<CrisisMap>,
    currentMapId: String?,
    onMapClick: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.lg),
        contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        items(maps) { map ->
            val isSelected = map.id == currentMapId
            val chipBgColor by animateColorAsState(
                targetValue = if (isSelected) AppCard else Color.Transparent,
                label = "chipBg"
            )
            val textColor = if (isSelected) AppInk else AppDim
            val dotColor = if (map.type == MapType.OFFICIAL) AppBlue else AppAmber

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.lg))
                    .background(chipBgColor)
                    .clickable { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMapClick(map.id) 
                    }
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = map.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigation(
    currentTab: Int,
    activeMap: CrisisMap?,
    onTabSelect: (Int) -> Unit,
    onTriggerCoordAction: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val tabs = listOf(
        Triple("Harita", Icons.Default.Place, 0),
        Triple("Partiler", Icons.Default.List, 1),
        Triple("Bildirimler", Icons.Default.Notifications, 2),
        Triple("Ben", Icons.Default.Person, 3)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppSurface)
            .drawBehind {
                drawLine(
                    color = AppLine,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding()
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { (label, icon, index) ->
            val isSelected = currentTab == index && index != 2
            val color = if (isSelected) AppAcid else AppDim

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (index == 2) onTriggerCoordAction()
                        else onTabSelect(index)
                    }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
            }
        }
    }
}
