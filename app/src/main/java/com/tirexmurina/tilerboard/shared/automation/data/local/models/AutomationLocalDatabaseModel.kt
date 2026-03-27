package com.tirexmurina.tilerboard.shared.automation.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automations")
data class AutomationLocalDatabaseModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "linked_user_id")
    val linkedUserId: Long,
    val name: String,
    @ColumnInfo(name = "source_entity_id")
    val sourceEntityId: String,
    @ColumnInfo(name = "condition_type")
    val conditionType: String,
    @ColumnInfo(name = "condition_expected_value")
    val conditionExpectedValue: String,
    @ColumnInfo(name = "action_type")
    val actionType: String,
    @ColumnInfo(name = "action_payload")
    val actionPayload: String?,
    val enabled: Boolean
)
