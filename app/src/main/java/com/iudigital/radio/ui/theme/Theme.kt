package com.iudigital.radio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta inspirada en el dial de una radio de válvulas
val DialTeal = Color(0xFF0E2A2B)      // fondo
val DialPanel = Color(0xFF15393B)     // superficies
val DialIvory = Color(0xFFF2E3BC)     // texto y marcas del dial
val DialNeedle = Color(0xFFE4572E)    // aguja / acento
val DialMuted = Color(0xFF8FB0A8)     // texto secundario

private val Scheme = darkColorScheme(
    primary = DialNeedle,
    onPrimary = Color.White,
    background = DialTeal,
    onBackground = DialIvory,
    surface = DialPanel,
    onSurface = DialIvory,
    surfaceVariant = Color(0xFF1D4A4C),
    onSurfaceVariant = DialMuted,
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
        fontSize = 26.sp, lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
)

/** La identidad visual es oscura por diseño, independiente del tema del sistema. */
@Composable
fun IUDigitalRadioTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = AppTypography, content = content)
}
