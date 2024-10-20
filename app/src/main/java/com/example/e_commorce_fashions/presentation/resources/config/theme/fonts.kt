package com.example.e_commorce_fashions.presentation.resources.config.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.e_commorce_fashions.R


private val greatVibesRegular = Font(R.font.greatvibes_regular, weight = FontWeight.Normal, style = FontStyle.Normal)

private val poppinsRegular = Font(R.font.poppins_regular, weight = FontWeight.Normal, style = FontStyle.Normal)
private val poppinsMedium = Font(R.font.poppins_medium, weight = FontWeight.Medium, style = FontStyle.Normal)
private val poppinsSemiBold = Font(R.font.poppins_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal)
private val poppinsBold = Font(R.font.poppins_bold, weight = FontWeight.Bold, style = FontStyle.Normal)
private val poppinsBlack = Font(R.font.poppins_black, weight = FontWeight.Black, style = FontStyle.Normal)
private val poppinsLight = Font(R.font.poppins_light, weight = FontWeight.Light, style = FontStyle.Normal)
private val poppinsItalic = Font(R.font.poppins_italic, weight = FontWeight.Normal, style = FontStyle.Italic)


private val POPPINS_FONT_FAMILY by lazy {
    FontFamily(
        poppinsRegular,
        poppinsMedium,
        poppinsSemiBold,
        poppinsBold,
        poppinsBlack,
        poppinsLight,
        poppinsItalic
    )
}

private val GREAT_VIBES_FONT_FAMILY by lazy {
    FontFamily(
        greatVibesRegular
    )
}

val FontFamily.Companion.Poppins: FontFamily get() = POPPINS_FONT_FAMILY
val FontFamily.Companion.GreatVibes: FontFamily get() = GREAT_VIBES_FONT_FAMILY
