package theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import composefordesktop.resources.*
import org.jetbrains.compose.resources.Font

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val robotoFontFamily = getRobotoFamily()

    val ubuntuFontFamily = getUbuntuFamily()

    val ubuntuType = Typography(
        headlineLarge = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelMedium = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = ubuntuFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
    )

    val robotoType = Typography(
        headlineLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if (System.getProperty("os.name").lowercase() == "linux") ubuntuType else robotoType,
        content = content
    )
}

@Composable
fun getRobotoFamily(): FontFamily{
    val robotoFontFamily = FontFamily(
        Font(resource = Res.font.Roboto_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resource = Res.font.Roboto_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resource = Res.font.Roboto_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
    return robotoFontFamily
}

@Composable
fun getUbuntuFamily(): FontFamily{
    val ubuntuFontFamily = FontFamily(
        Font(resource = Res.font.Ubuntu_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resource = Res.font.Ubuntu_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resource = Res.font.Ubuntu_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
    return ubuntuFontFamily
}

@Composable
fun getJetbrainsMonoFamily(): FontFamily{
    val jetbrainsMonoFamily = FontFamily(
        Font(resource = Res.font.JetBrainsMono_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resource = Res.font.JetBrainsMono_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resource = Res.font.JetBrainsMono_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
    return jetbrainsMonoFamily
}