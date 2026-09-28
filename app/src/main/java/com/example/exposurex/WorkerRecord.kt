package com.example.exposurex

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers")
data class WorkerRecord(
    @PrimaryKey val workerId: String,
    val name: String,
    val registeredOn: String
)