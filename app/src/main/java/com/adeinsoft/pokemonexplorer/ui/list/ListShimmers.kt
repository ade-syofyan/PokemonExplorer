package com.adeinsoft.pokemonexplorer.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adeinsoft.pokemonexplorer.ui.components.ShimmerCircle
import com.adeinsoft.pokemonexplorer.ui.components.ShimmerLine

@Composable
fun ListShimmerPlaceholder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
    ) {
        repeat(8) { PokemonRowShimmer() }
    }
}

@Composable
private fun PokemonRowShimmer() {
    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerCircle(size = 56.dp)

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerLine(width = 140.dp, height = 16.dp)
                ShimmerLine(width = 90.dp,  height = 12.dp)
            }

            ShimmerCircle(size = 28.dp)
        }
    }
    Spacer(Modifier.height(4.dp))
}
