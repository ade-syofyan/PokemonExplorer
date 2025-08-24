package com.adeinsoft.pokemonexplorer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberShimmerBrush(): Brush {
    val base = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset.Zero,
        end = Offset(x = anim, y = anim)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(rememberShimmerBrush())
    )
}

@Composable
fun ShimmerLine(
    width: Dp,
    height: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    ShimmerBox(
        modifier = Modifier
            .size(width = width, height = height),
        shape = shape
    )
}

@Composable
fun ShimmerCircle(size: Dp) {
    ShimmerBox(
        modifier = Modifier.size(size),
        shape = CircleShape
    )
}
