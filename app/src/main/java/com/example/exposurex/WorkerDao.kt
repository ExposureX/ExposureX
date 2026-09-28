package com.example.exposurex

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkerDao {
    @Insert
    suspend fun insertWorker(worker: WorkerRecord)

    @Query("SELECT * FROM workers ORDER BY name ASC")
    suspend fun getAllWorkers(): List<WorkerRecord>

    @Query("SELECT * FROM workers WHERE workerId = :id LIMIT 1")
    suspend fun getWorkerById(id: String): WorkerRecord?
}