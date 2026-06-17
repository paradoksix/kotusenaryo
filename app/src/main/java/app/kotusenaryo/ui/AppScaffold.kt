package app.kotusenaryo.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kotusenaryo.ui.theme.*

@Composable
fun AppScaffold(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (state.isOnboarded) {
                NavigationBar(containerColor = AppSurface) {
                    val tabs = listOf("Harita", "Partiler", "Bildirimler", "Profil")
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = state.currentTab == index,
                            onClick = { viewModel.setTab(index) },
                            icon = { Text(title.take(1)) },
                            label = { Text(title) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = AppCard,
                                selectedIconColor = AppAcid,
                                unselectedIconColor = AppDim
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(AppBg)) {
            AnimatedContent(
                targetState = state.isOnboarded,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)).togetherWith(fadeOut(animationSpec = tween(500)))
                },
                label = "onboard_transition"
            ) { isOnboarded ->
                if (!isOnboarded) {
                    app.kotusenaryo.ui.screens.OnboardingScreen(
                        isVerifying = state.isVerifyingCode,
                        error = state.verificationError,
                        onVerifyClick = { viewModel.verifyCode(it) }
                    )
                } else {
                    AnimatedContent(
                        targetState = state.currentTab,
                        transitionSpec = {
                            fadeIn(tween(300)).togetherWith(fadeOut(tween(300)))
                        },
                         label = "tab_transition"
                    ) { tab ->
                         when(tab) {
                             0 -> app.kotusenaryo.ui.screens.MapScreen(viewModel)
                             1 -> app.kotusenaryo.ui.screens.PartyScreen(viewModel) { viewModel.showNotification(it) }
                             2 -> app.kotusenaryo.ui.screens.NotificationScreen(state.notifications) { viewModel.markNotificationRead(it) }
                             3 -> app.kotusenaryo.ui.screens.ProfileScreen(viewModel)
                         }
                    }
                }
            }
        }
    }
}
