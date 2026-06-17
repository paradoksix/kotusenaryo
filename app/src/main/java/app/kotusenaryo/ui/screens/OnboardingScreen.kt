package app.kotusenaryo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kotusenaryo.ui.theme.*

@Composable
fun OnboardingScreen(isVerifying: Boolean, error: String?, onVerifyClick: (String) -> Unit) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(AppBg).padding(Spacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SİVİL DAYANIŞMA AĞI", style = MaterialTheme.typography.labelMedium, color = AppAcid)
        Spacer(modifier = Modifier.height(8.dp))
        Text("KÖTÜ SENARYO", style = MaterialTheme.typography.displayLarge, color = AppText)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Güvenilir toplulukların kriz anında harita üzerinden dayanışmayı koordine ettiği kapalı topluluk uygulaması.", style = MaterialTheme.typography.bodyLarge, color = AppDim)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Davet Kodu") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppAcid,
                unfocusedBorderColor = AppLine,
                focusedTextColor = AppText,
                unfocusedTextColor = AppText
            )
        )
        if (error != null) {
            Text(error, color = AppRed, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 4.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onVerifyClick(code) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppAcid, contentColor = AppBg)
        ) {
            if (isVerifying) CircularProgressIndicator(color = AppBg)
            else Text("SİSTEME GİRİŞ")
        }
    }
}
