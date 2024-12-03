package com.tirexmurina.tilerboard.features.home.ui.tiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum

@Composable
fun SimpleBinaryTile(state : BinaryOnOffEnum?) {
    Column (
        modifier = Modifier.fillMaxHeight()
    ){
        Row (
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = R.drawable.baseline_handyman_24),
                contentDescription = ""
            )
            Text(
                text = "$state",
                fontSize = 20.sp
            )
        }

    }
}