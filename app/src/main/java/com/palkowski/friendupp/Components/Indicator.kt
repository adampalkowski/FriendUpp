package com.palkowski.friendupp.Components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palkowski.friendupp.ui.theme.FriendUppTheme


fun generatePath(data:List<Int>,size:Size):Path{
    val path=Path()

    data.forEachIndexed{i,value->
        val verticalSize = size.width / (data.size + 1)
        val startX = verticalSize * (i + 1)

        // Map the value to the available height of the canvas
        val startY = size.height * (1f - value.toFloat() / data.maxOf { it })

        if (i == 0) {
            path.moveTo(startX, startY)
        } else {
            // Calculate the control points for the cubic Bezier curve
            val prevValue = data[i - 1]
            val prevY = size.height * (1f - prevValue.toFloat() / data.maxOf { it })
            val nextValue = if (i < data.size - 1) data[i + 1] else value
            val nextY = size.height * (1f - nextValue.toFloat() / data.maxOf { it })

            val controlPoint1X = startX - verticalSize / 2
            val controlPoint1Y = (startY + prevY) / 2
            val controlPoint2X = startX - verticalSize / 2
            val controlPoint2Y = (startY + nextY) / 2

            path.cubicTo(
                controlPoint1X, controlPoint1Y,
                controlPoint2X, controlPoint2Y,
                startX, startY
            )
        }
    }
    return path
}

@Preview
@Composable
fun AnimatedLinesCanvas() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White).drawBehind {

                drawLine(Color(0xffFFDB5A),cap=StrokeCap.Butt, start = Offset(x=-80.dp.toPx(),y=60.dp.toPx()), end = Offset(x=size.width-80.dp.toPx(),y=-80.dp.toPx()), strokeWidth = 60.dp.toPx())

                drawLine(Color(0xffFFDB5A),cap=StrokeCap.Butt, start = Offset(x=size.width-100.dp.toPx(),y=-20.dp.toPx()), end = Offset(x=size.width+80.dp.toPx(),y=120.dp.toPx()), strokeWidth = 48.dp.toPx())
                drawLine(Color(0xffBCABFF),cap=StrokeCap.Butt, start = Offset(x=-120.dp.toPx(),y=100.dp.toPx()), end = Offset(x=size.width,y=-40.dp.toPx()), strokeWidth = 48.dp.toPx())
                drawLine(Color(0xffBCABFF),cap=StrokeCap.Butt, start = Offset(x=size.width-120.dp.toPx(),y=-40.dp.toPx()), end = Offset(x=size.width+100.dp.toPx(),y=100.dp.toPx()), strokeWidth = 60.dp.toPx())


            }
    ) {

    }
}





