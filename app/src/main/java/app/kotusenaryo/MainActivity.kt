package app.kotusenaryo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.kotusenaryo.ui.AppScaffold
import app.kotusenaryo.ui.AppViewModel
import app.kotusenaryo.ui.theme.KotuSenaryoTheme

class MainActivity : ComponentActivity() {
  // Application entry point
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    org.maplibre.android.MapLibre.getInstance(this)
    enableEdgeToEdge()
    setContent {
      KotuSenaryoTheme {
        val appViewModel: AppViewModel = viewModel()
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
          ) {
            composable("home") {
              AppScaffold(viewModel = appViewModel)
            }
          }
        }
      }
    }
  }
}
