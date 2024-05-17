package meshki.studio.negarname.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import meshki.studio.negarname.R

object AppFont {
        val Vazirmatn = FontFamily(
                Font(R.font.vazirmatn_black, FontWeight.Black),
                Font(R.font.vazirmatn_bold, FontWeight.Bold),
                Font(R.font.vazirmatn_medium, FontWeight.Medium),
                Font(R.font.vazirmatn_semi_bold, FontWeight.SemiBold),
                Font(R.font.vazirmatn_extra_bold, FontWeight.ExtraBold),
                Font(R.font.vazirmatn_extra_light, FontWeight.ExtraLight),
                Font(R.font.vazirmatn_light, FontWeight.Light),
                Font(R.font.vazirmatn_thin, FontWeight.Thin),
                Font(R.font.vazirmatn_regular, FontWeight.Normal)
        )
}

val defaultTypography = Typography()
val rtlTypography = Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.Vazirmatn),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.Vazirmatn),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.Vazirmatn),

        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.Vazirmatn),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.Vazirmatn),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.Vazirmatn),

        titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.Vazirmatn),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.Vazirmatn),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.Vazirmatn),

        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.Vazirmatn),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.Vazirmatn),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.Vazirmatn),

        labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.Vazirmatn),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.Vazirmatn),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.Vazirmatn)
)