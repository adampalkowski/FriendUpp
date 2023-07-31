package com.palkowski.friendupp.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.palkowski.friendupp.R

public val Lexend = FontFamily(
    Font(R.font.lexenddecalight, FontWeight.Light),
    Font(R.font.lexenddecaregular ,FontWeight.Normal),
    Font(R.font.lexenddecamedium, FontWeight.Medium),
    Font(R.font.lexenddecasemibold, FontWeight.SemiBold),
    Font(R.font.lexenddecaextralight, FontWeight.ExtraLight),
    Font(R.font.lexenddecaextrabold, FontWeight.ExtraBold),
    Font(R.font.lexenddecathin, FontWeight.Thin)
)
public val Pacifico = FontFamily(
    Font(R.font.pacifico, FontWeight.Normal),
)

val Typography = androidx.compose.material.Typography(
    h1 = TextStyle(
        fontFamily = Lexend,
        fontSize = 96.sp,
        fontWeight = FontWeight.Light,

        ),
    h2 = TextStyle(
        fontFamily = Lexend,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    ),
    h3 = TextStyle(
        fontFamily = Lexend,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,

        ),
    h4 = TextStyle(
        fontFamily = Lexend,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    h5 = TextStyle(
        fontFamily = Lexend,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    ),
    h6 = TextStyle(
        fontFamily = Lexend,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraLight,
    ),
    subtitle1 = TextStyle(
        fontFamily = Lexend,
        fontSize = 10.sp,
        fontWeight = FontWeight.Light,

        ),
    subtitle2 = TextStyle(
        fontFamily = Lexend,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,

        ),
    body1 = TextStyle(
        fontFamily = Lexend,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,

        ),
    body2 = TextStyle(
        fontFamily = Lexend,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,

        ),
    button = TextStyle(
        fontFamily = Lexend,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    ),
    caption = TextStyle(
        fontFamily = Lexend,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,

        ),
    overline = TextStyle(
        fontFamily = Lexend,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,

        )
)