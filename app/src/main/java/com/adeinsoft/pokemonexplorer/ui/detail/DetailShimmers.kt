package com.adeinsoft.pokemonexplorer.ui.detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.adeinsoft.pokemonexplorer.ui.components.ShimmerBox
import com.adeinsoft.pokemonexplorer.ui.components.ShimmerCircle
import com.adeinsoft.pokemonexplorer.ui.components.ShimmerLine

@Composable
fun DetailShimmerPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Header card: image, name, types + favorite on top-end
        Box(Modifier.fillMaxWidth()) {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // image (Rounded 16 like ShimmerAsyncImage)
                    ShimmerBox(
                        Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    // name (headlineSmall-ish)
                    ShimmerLine(width = 180.dp, height = 28.dp)
                    // types (AssistChip row)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(2) {
                            ShimmerBox(
                                Modifier
                                    .height(32.dp)
                                    .width(84.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }
                    }
                }
            }

            // favorite button placeholder (TopEnd, circular)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                ShimmerCircle(size = 40.dp)
            }
        }

        // About + Abilities
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // "About" title (titleMedium)
                ShimmerLine(width = 100.dp, height = 22.dp)

                // 3 info tiles (Height, Weight, Base EXP)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(3) {
                        ElevatedCard(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 80.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // label (labelLarge)
                                ShimmerLine(width = 56.dp, height = 12.dp)
                                // value (titleMedium)
                                ShimmerLine(width = 72.dp, height = 18.dp)
                            }
                        }
                    }
                }

                // "Abilities" subtitle (titleSmall)
                ShimmerLine(width = 80.dp, height = 16.dp)

                // abilities chips (FilterChip row)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        ShimmerBox(
                            Modifier
                                .height(32.dp)
                                .width(110.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    }
                }
            }
        }

        // Base Stats
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "Base Stats" title
                ShimmerLine(width = 120.dp, height = 22.dp)

                repeat(6) {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // stat name (label)
                            ShimmerLine(width = 80.dp, height = 12.dp)
                            // stat value (label)
                            ShimmerLine(width = 28.dp, height = 12.dp)
                        }
                        // progress bar track (8dp high, rounded 4)
                        ShimmerBox(
                            Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        // Pokédex Entry (flavor text)
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // title
                ShimmerLine(width = 140.dp, height = 22.dp)
                // 3–4 lines of body text
                ShimmerLine(width = 280.dp, height = 12.dp)
                ShimmerLine(width = 260.dp, height = 12.dp)
                ShimmerLine(width = 240.dp, height = 12.dp)
                ShimmerLine(width = 200.dp, height = 12.dp)
            }
        }

        // Evolution
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // title
                ShimmerLine(width = 100.dp, height = 22.dp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ShimmerCircle(size = 72.dp)
                            Spacer(Modifier.height(6.dp))
                            ShimmerLine(width = 72.dp, height = 12.dp) // evolution name
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}


