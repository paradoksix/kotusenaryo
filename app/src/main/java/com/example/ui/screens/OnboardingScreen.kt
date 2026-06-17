package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.components.core.PrimaryButton
import kotlinx.coroutines.delay

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning

@Composable
fun OnboardingScreen(
    isVerifying: Boolean,
    error: String?,
    onVerifyClick: (String) -> Unit
) {
    var step1 by remember { mutableStateOf(false) }
    var step2 by remember { mutableStateOf(false) }
    var step3 by remember { mutableStateOf(false) }
    var step4 by remember { mutableStateOf(false) }

    var code by remember { mutableStateOf("YLDZ24") }

    LaunchedEffect(Unit) {
        delay(300)
        step1 = true
        delay(400)
        step2 = true
        delay(600)
        step3 = true
        delay(400)
        step4 = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        SurfaceColor,
                        MapBackground
                    ),
                    radius = 1500f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal),
            horizontalAlignment = Alignment.Start
        ) {
            AnimatedVisibility(visible = step1, enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 20 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f))) {
                Text(
                    text = "SİVİL DAYANIŞMA AĞI",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppDim,
                    letterSpacing = 2.sp
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))

            AnimatedVisibility(visible = step2, enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 20 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f))) {
                Text(
                    text = buildAnnotatedString {
                        append("KÖTÜ SENARYO")
                        withStyle(SpanStyle(color = AppRed)) {
                            append(".")
                        }
                    },
                    style = MaterialTheme.typography.displayLarge,
                    color = AppInk,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            AnimatedVisibility(visible = step3, enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 20 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f))) {
                Text(
                    text = "Güvenilir toplulukların kriz anında harita üzerinden dayanışmayı koordine ettiği kapalı topluluk uygulaması.\n\nErişim için davet kodu zorunludur.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppDim
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(visible = step4, enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 20 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f))) {
                Column {
                    Text(
                        text = "Davet / Referans Kodu",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppDim
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.titleLarge.copy(letterSpacing = 4.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppInk,
                            unfocusedTextColor = AppInk,
                            focusedBorderColor = AppAcid,
                            unfocusedBorderColor = AppLine,
                            cursorColor = AppAcid
                        ),
                        singleLine = true,
                        enabled = !isVerifying
                    )
                    
                    AnimatedVisibility(visible = error != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = Spacing.sm)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = AppRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = error ?: "", color = AppRed, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryButton(
                        text = if (isVerifying) "Doğrulanıyor..." else "Ağa Katıl",
                        onClick = { onVerifyClick(code) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = code.isNotBlank() && !isVerifying
                    )
                }
            }
        }
    }
}
