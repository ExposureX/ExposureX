package com.example.exposurex

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SupervisorActivity : AppCompatActivity() {

    private var workers: List<WorkerRecord> = emptyList()
    private var allScans: List<ScanRecord> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor)

        val listView: ListView = findViewById(R.id.workerListView)
        val summaryText: TextView = findViewById(R.id.summaryText)
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            workers = db.workerDao().getAllWorkers()
            allScans = db.scanDao().getAllScans()

            val flaggedCount = workers.count { w ->
                allScans.any { it.workerId == w.workerId && it.doseValue >= FLAG_THRESHOLD }
            }
            summaryText.text =
                "Workers: ${workers.size}  |  Scans: ${allScans.size}  |  Flagged: $flaggedCount"

            val rows = workers.map { w ->
                val scans = allScans.filter { it.workerId == w.workerId }
                val latest = scans.firstOrNull()
                val flag = if (scans.any { it.doseValue >= FLAG_THRESHOLD }) "⚠ " else ""
                val latestText = if (latest != null) {
                    "${latest.doseLevel} (${latest.doseValue}%)"
                } else "No scans yet"
                "$flag${w.name}  (${w.workerId})\nScans: ${scans.size}  |  Latest: $latestText"
            }

            listView.adapter =
                ArrayAdapter(this@SupervisorActivity, R.layout.item_row, R.id.rowText, rows)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val worker = workers[position]
            val scans = allScans.filter { it.workerId == worker.workerId }
            val log = if (scans.isEmpty()) "No scans recorded yet."
            else scans.joinToString("\n") { "${it.timestamp}  -  ${it.doseLevel} (${it.doseValue}%)" }

            AlertDialog.Builder(this)
                .setTitle("${worker.name} (${worker.workerId})")
                .setMessage(log)
                .setPositiveButton("Close", null)
                .show()
        }
    }

    companion object {
        // Placeholder threshold: replace with limits from IS 15200 / OISD once calibrated
        private const val FLAG_THRESHOLD = 75
    }
}