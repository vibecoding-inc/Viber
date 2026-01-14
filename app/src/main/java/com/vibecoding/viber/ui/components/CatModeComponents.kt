package com.vibecoding.viber.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val speedY: Float,
    val speedX: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onAnimationEnd: () -> Unit = {}
) {
    var particles by remember { mutableStateOf(listOf<ConfettiParticle>()) }
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_progress"
    )

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val colors = listOf(
                Color(0xFFFF6B35), // Orange
                Color(0xFFF7931E), // Yellow
                Color(0xFF00B4D8), // Blue
                Color(0xFFFF006E), // Pink
                Color(0xFF8338EC), // Purple
                Color(0xFF06FFA5), // Green
            )
            
            particles = List(50) {
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = -0.1f,
                    color = colors.random(),
                    size = Random.nextFloat() * 10f + 5f,
                    rotation = Random.nextFloat() * 360f,
                    speedY = Random.nextFloat() * 0.5f + 0.3f,
                    speedX = (Random.nextFloat() - 0.5f) * 0.2f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f
                )
            }
        } else {
            particles = emptyList()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = particle.y + animationProgress * particle.speedY
            val currentX = particle.x + animationProgress * particle.speedX
            val currentRotation = particle.rotation + animationProgress * particle.rotationSpeed * 360f

            if (currentY < 1.2f) {
                rotate(currentRotation, pivot = Offset(
                    currentX * size.width,
                    currentY * size.height
                )) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(
                            currentX * size.width - particle.size / 2,
                            currentY * size.height - particle.size / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(particle.size, particle.size)
                    )
                }
            }
        }
    }
}

@Composable
fun CatEmoji(
    modifier: Modifier = Modifier,
    isAnimating: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cat")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cat_scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cat_rotation"
    )

    if (isAnimating) {
        Canvas(modifier = modifier.size(80.dp)) {
            rotate(rotation) {
                val scaledSize = 80.dp.toPx() * scale
                // Drawing a simple cat face
                // Head
                drawCircle(
                    color = Color(0xFFFF9E5E),
                    radius = scaledSize / 2,
                    center = center
                )
                
                // Left ear
                val earPath = Path().apply {
                    moveTo(center.x - scaledSize / 3, center.y - scaledSize / 3)
                    lineTo(center.x - scaledSize / 2.5f, center.y - scaledSize / 1.5f)
                    lineTo(center.x - scaledSize / 5, center.y - scaledSize / 3)
                    close()
                }
                drawPath(earPath, color = Color(0xFFFF9E5E))
                
                // Right ear
                val earPath2 = Path().apply {
                    moveTo(center.x + scaledSize / 3, center.y - scaledSize / 3)
                    lineTo(center.x + scaledSize / 2.5f, center.y - scaledSize / 1.5f)
                    lineTo(center.x + scaledSize / 5, center.y - scaledSize / 3)
                    close()
                }
                drawPath(earPath2, color = Color(0xFFFF9E5E))
                
                // Left eye
                drawCircle(
                    color = Color.Black,
                    radius = scaledSize / 10,
                    center = Offset(center.x - scaledSize / 4, center.y - scaledSize / 8)
                )
                
                // Right eye
                drawCircle(
                    color = Color.Black,
                    radius = scaledSize / 10,
                    center = Offset(center.x + scaledSize / 4, center.y - scaledSize / 8)
                )
                
                // Nose
                drawCircle(
                    color = Color(0xFFFF6B9D),
                    radius = scaledSize / 15,
                    center = Offset(center.x, center.y + scaledSize / 8)
                )
            }
        }
    }
}

@Composable
fun CatModeOverlay(
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    if (isActive) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val maxWidthDp = maxWidth
            val maxHeightDp = maxHeight
            
            // Multiple cats floating around
            for (i in 0..4) {
                val offsetX = remember { Random.nextFloat() }
                val offsetY = remember { Random.nextFloat() }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = maxWidthDp * offsetX * 0.8f, // Use 80% of width to keep cats visible
                            top = maxHeightDp * offsetY * 0.8f   // Use 80% of height to keep cats visible
                        )
                ) {
                    CatEmoji(isAnimating = true)
                }
            }
        }
    }
}
