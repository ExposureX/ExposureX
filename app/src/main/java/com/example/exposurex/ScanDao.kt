package com.example.exposurex

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScanDao {
    @Insert
    suspend fun insertScan(scan: ScanRecord)

    @Query("SELECT * FROM scan_records ORDER BY id DESC")
    suspend fun getAllScans(): List<ScanRecord>

    @Query("SELECT * FROM scan_records WHERE workerId = :workerId ORDER BY id DESC")
    suspend fun getScansForWorker(workerId: String): List<ScanRecord>
}