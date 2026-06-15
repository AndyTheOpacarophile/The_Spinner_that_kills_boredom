package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ActivityEntity
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VibeRouletteWheel(
    activities: List<ActivityEntity>,
    rotationProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(280.dp)
            .background(Color.Transparent)
            .testTag("vibe_roulette_wheel_container"),
        contentAlignment = Alignment.Center
    ) {
        // External ring shadow/border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(8.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
        )

        // The spinning wheel canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .graphicsLayer {
                    rotationZ = rotationProvider()
                }
                .testTag("vibe_roulette_canvas")
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f

            // Clean background fill
            drawCircle(
                color = if (activities.isEmpty()) Color.LightGray else Color(0xFF1E293B),
                radius = radius
            )

            if (activities.isNotEmpty()) {
                val sliceDegrees = 360f / activities.size
                
                activities.forEachIndexed { index, activity ->
                    val startAngle = index * sliceDegrees - 90f // align first section at top
                    val categoryColor = when (activity.category) {
                        "灵感漫游" -> ColorIndoorCalm
                        "居室整活" -> ColorIndoorActive
                        "城市漂流" -> ColorOutdoorCalm
                        "肉身出逃" -> ColorOutdoorActive
                        else -> PrimaryTeal
                    }

                    // Alternate shades for clean readability
                    val drawColor = if (index % 2 == 0) {
                        categoryColor.copy(alpha = 0.9f)
                    } else {
                        categoryColor.copy(alpha = 0.65f)
                    }

                    // Draw the slice arc
                    drawArc(
                        color = drawColor,
                        startAngle = startAngle,
                        sweepAngle = sliceDegrees,
                        useCenter = true,
                        size = size
                    )

                    // Draw thin separator lines
                    val lineRad = (startAngle * PI / 180f)
                    val edgeX = center.x + radius * cos(lineRad).toFloat()
                    val edgeY = center.y + radius * sin(lineRad).toFloat()
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = center,
                        end = Offset(edgeX, edgeY),
                        strokeWidth = 2f
                    )

                    // Draw the Activity Title radially in the middle of each slice
                    val midAngle = startAngle + sliceDegrees / 2f
                    rotate(degrees = midAngle, pivot = center) {
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = when {
                                activities.size > 12 -> radius * 0.065f
                                activities.size > 8 -> radius * 0.075f
                                activities.size > 5 -> radius * 0.09f
                                else -> radius * 0.105f
                            }
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.LEFT
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        }

                        // Max width available for text before reaching near outer rim (offset from center and margin at end)
                        val textStartX = center.x + radius * 0.28f
                        val maxAllowedWidth = (center.x + radius * 0.88f) - textStartX

                        // Break text precisely to prevent any spilling beyond the perimeter
                        val displayTitle: String
                        val measuredWidths = FloatArray(1)
                        val charCount = textPaint.breakText(
                            activity.title,
                            true,
                            maxAllowedWidth,
                            measuredWidths
                        )
                        displayTitle = if (charCount < activity.title.length) {
                            activity.title.take(charCount - 1) + "…"
                        } else {
                            activity.title
                        }

                        val textY = center.y + (textPaint.textSize / 3f)

                        drawContext.canvas.nativeCanvas.drawText(
                            displayTitle,
                            textStartX,
                            textY,
                            textPaint
                        )
                    }
                }

                // Draw center neon aesthetic indicator pin
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = radius * 0.18f
                )
                drawCircle(
                    color = PrimaryTeal,
                    radius = radius * 0.15f,
                    style = Stroke(width = 4f)
                )
            } else {
                // Empty state wheel
                drawCircle(
                    color = Color.Gray,
                    radius = radius,
                    style = Stroke(width = 2f)
                )
            }
        }

        // Static overlay pointer (Top indicator) with dynamic mechanical deflection ticking
        val rotationVal = rotationProvider()
        val pointerTilt = if (activities.isNotEmpty()) {
            val sliceCount = activities.size
            val sliceDegrees = 360f / sliceCount
            val remainder = rotationVal % sliceDegrees
            val halfSlice = sliceDegrees / 2f
            val delta = remainder - halfSlice
            val ratio = delta / halfSlice
            val absRatio = if (ratio < 0) -ratio else ratio
            if (absRatio > 0.75f) {
                val factor = (absRatio - 0.75f) / 0.25f
                val sign = if (ratio < 0) -1f else 1f
                sign * factor * 12f // deflect up to 12 degrees side to side
            } else {
                0f
            }
        } else {
            0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropUp,
                contentDescription = "Pointer",
                tint = CrimsonAccent,
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = (-14).dp)
                    .graphicsLayer {
                        // Blend standard upside-down rotation (180 deg) with interactive tick deflection
                        rotationZ = 180f + pointerTilt
                    }
            )
        }
    }
}

@Composable
fun VibeCompletionProgressBar(
    indoorCalm: Int,
    indoorActive: Int,
    outdoorCalm: Int,
    outdoorActive: Int,
    modifier: Modifier = Modifier
) {
    val total = (indoorCalm + indoorActive + outdoorCalm + outdoorActive).toFloat()
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), CircleShape),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (total == 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Gray.copy(alpha = 0.4f), CircleShape)
                )
            } else {
                val weightIC = indoorCalm / total
                val weightIA = indoorActive / total
                val weightOC = outdoorCalm / total
                val weightOA = outdoorActive / total

                if (weightIC > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weightIC)
                            .background(ColorIndoorCalm)
                    )
                }
                if (weightIA > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weightIA)
                            .background(ColorIndoorActive)
                    )
                }
                if (weightOC > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weightOC)
                            .background(ColorOutdoorCalm)
                    )
                }
                if (weightOA > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weightOA)
                            .background(ColorOutdoorActive)
                    )
                }
            }
        }
    }
}
