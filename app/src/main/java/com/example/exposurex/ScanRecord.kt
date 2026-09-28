package com.example.exposurex

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_records")
data class ScanRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workerId: String,
    val timestamp: String,
    val doseLevel: String,
    val doseValue: Int,
    val isValid: Boolean
)