package com.tirexmurina.tilerboard.features.util.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor

@Composable
fun SensorCard(
    sensor: Sensor,
    onClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = { onClick(sensor.entityId) }),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            SensorField(label = "Entity ID", value = sensor.entityId)
            /*SensorField(label = "Last Changed", value = sensor.lastChanged)
            SensorField(label = "Last Updated", value = sensor.lastUpdated)*/
            SensorField(label = "State", value = sensor.state)
            SensorField(label = "Device Class", value = sensor.deviceClass)
            SensorField(label = "Friendly Name", value = sensor.friendlyName)
            SensorField(label = "Unit", value = sensor.unitOfMeasurement)
        }
    }
}

@Composable
private fun SensorField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}