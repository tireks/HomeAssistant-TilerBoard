package com.tirexmurina.tilerboard.features.util.tileCards

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType

@Composable
fun TileCardsGrid(tiles: List<Tile>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(tiles) { tile ->
            when (tile.type) {
                is TileType.SimpleTemperature -> SimpleTemperatureSensorTileCard(
                    title = tile.name ?: tile.sensor.friendlyName,
                    state = tile.type.temperature
                )
                is TileType.SimpleBinaryOnOff -> SimpleBinaryTileCard(
                    title = tile.name ?: tile.sensor.friendlyName,
                    state = tile.type.state
                )
                is TileType.SimpleHumidity -> SimpleHumiditySensorTileCard(
                    title = tile.name ?: tile.sensor.friendlyName,
                    state = tile.type.humidity
                )
                is TileType.SimpleNoTypeRaw -> SimpleNoTypeTileCard(
                    title = tile.name ?: tile.sensor.friendlyName,
                    state = tile.type.state
                )
            }
        }
    }
}