package com.adeinsoft.pokemonexplorer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image

@Composable
fun ShimmerAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier,
    shape: Shape,
    contentScale: ContentScale
) {
    val painter = rememberAsyncImagePainter(model = model)
    val state = painter.state

    Box(modifier = modifier.clip(shape)) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
        if (state is AsyncImagePainter.State.Loading) {
            ShimmerBox(Modifier.fillMaxSize(), shape = shape)
        }
    }
}
