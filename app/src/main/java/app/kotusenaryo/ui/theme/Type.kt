package app.kotusenaryo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import app.kotusenaryo.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val ArchivoFont = GoogleFont("Archivo")
val SplineSansMonoFont = GoogleFont("Spline Sans Mono")

val Archivo = FontFamily(
    Font(googleFont = ArchivoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = ArchivoFont, fontProvider = provider, weight = FontWeight.Bold)
)

val SplineMono = FontFamily(
    Font(googleFont = SplineSansMonoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = SplineSansMonoFont, fontProvider = provider, weight = FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = AppText
    ),
    titleLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = AppText
    ),
    titleMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = AppText
    ),
    bodyLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = AppText
    ),
    bodyMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = AppDim
    ),
    labelLarge = TextStyle(
        fontFamily = SplineMono,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = AppText
    ),
    labelMedium = TextStyle(
        fontFamily = SplineMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = AppDim
    ),
    labelSmall = TextStyle(
        fontFamily = SplineMono,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        color = AppDim
    )
)
