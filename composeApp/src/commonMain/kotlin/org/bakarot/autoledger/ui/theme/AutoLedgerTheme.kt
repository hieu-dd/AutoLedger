package org.bakarot.autoledger.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ManropeFallback = FontFamily.SansSerif
private val WorkSansFallback = FontFamily.SansSerif

private val AutoLedgerColorScheme: ColorScheme =
    lightColorScheme(
        primary = Color(0xFF004D64),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF006684),
        onPrimaryContainer = Color(0xFFA2E1FF),
        secondary = Color(0xFF526167),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD2E2E9),
        onSecondaryContainer = Color(0xFF56656B),
        tertiary = Color(0xFF713600),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF954900),
        onTertiaryContainer = Color(0xFFFFCEAF),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF93000A),
        background = Color(0xFFF7F9FC),
        onBackground = Color(0xFF181C1E),
        surface = Color(0xFFF7F9FC),
        onSurface = Color(0xFF181C1E),
        surfaceVariant = Color(0xFFE0E3E5),
        onSurfaceVariant = Color(0xFF3F484D),
        outline = Color(0xFF70787E),
        outlineVariant = Color(0xFFBFC8CD),
        inverseSurface = Color(0xFF2D3133),
        inverseOnSurface = Color(0xFFEFF1F3),
        inversePrimary = Color(0xFF87D0F2),
        surfaceTint = Color(0xFF016684),
    )

private val AutoLedgerTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = ManropeFallback,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 48.sp,
                lineHeight = 54.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = ManropeFallback,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 46.sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = ManropeFallback,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = ManropeFallback,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontStyle = FontStyle.Italic,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = WorkSansFallback,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = WorkSansFallback,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = WorkSansFallback,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = WorkSansFallback,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.8.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = WorkSansFallback,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
            ),
    )

private val AutoLedgerShapes =
    Shapes(
        extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
        small = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        large = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    )

@Immutable
data class AutoLedgerExtraColors(
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val secondaryFixed: Color,
    val primaryFixed: Color,
    val tertiaryFixedDim: Color,
)

private val LocalExtraColors =
    staticCompositionLocalOf {
        AutoLedgerExtraColors(
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF2F4F6),
            surfaceContainer = Color(0xFFECEEF1),
            secondaryFixed = Color(0xFFD5E5EC),
            primaryFixed = Color(0xFFBEE9FF),
            tertiaryFixedDim = Color(0xFFFFB786),
        )
    }

@Composable
fun AutoLedgerTheme(content: @Composable () -> Unit) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalExtraColors provides
            AutoLedgerExtraColors(
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF2F4F6),
                surfaceContainer = Color(0xFFECEEF1),
                secondaryFixed = Color(0xFFD5E5EC),
                primaryFixed = Color(0xFFBEE9FF),
                tertiaryFixedDim = Color(0xFFFFB786),
            ),
    ) {
        MaterialTheme(
            colorScheme = AutoLedgerColorScheme,
            typography = AutoLedgerTypography,
            shapes = AutoLedgerShapes,
            content = content,
        )
    }
}

object AutoLedgerDesign {
    val extraColors: AutoLedgerExtraColors
        @Composable get() = LocalExtraColors.current
}
