package com.tirexmurina.tilerboard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private fun TextStyle.withAddedFontSize(delta: TextUnit): TextStyle {
    return if (fontSize.isSp && delta.isSp) copy(fontSize = (fontSize.value + delta.value).sp) else this
}

private fun Typography.withAddedFontSize(delta: TextUnit): Typography {
    return copy(
        displayLarge = displayLarge.withAddedFontSize(delta),
        displayMedium = displayMedium.withAddedFontSize(delta),
        displaySmall = displaySmall.withAddedFontSize(delta),
        headlineLarge = headlineLarge.withAddedFontSize(delta),
        headlineMedium = headlineMedium.withAddedFontSize(delta),
        headlineSmall = headlineSmall.withAddedFontSize(delta),
        titleLarge = titleLarge.withAddedFontSize(delta),
        titleMedium = titleMedium.withAddedFontSize(delta),
        titleSmall = titleSmall.withAddedFontSize(delta),
        bodyLarge = bodyLarge.withAddedFontSize(delta),
        bodyMedium = bodyMedium.withAddedFontSize(delta),
        bodySmall = bodySmall.withAddedFontSize(delta),
        labelLarge = labelLarge.withAddedFontSize(delta),
        labelMedium = labelMedium.withAddedFontSize(delta),
        labelSmall = labelSmall.withAddedFontSize(delta)
    )
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
).withAddedFontSize(3.sp)
