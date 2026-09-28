package com.example.exposurex

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val listView: ListView = findViewById(R.id.historyListView)
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val scans = db.scanDao().getAllScans()
            val displayList = scans.map {
                "${it.workerId}  |  ${it.timestamp}\n${it.doseLevel} (${it.doseValue}%)"
            }
            val adapter = ArrayAdapter(this@HistoryActivity, android.R.layout.simple_list_item_2, android.R.id.text1, displayList)
            listView.adapter = adapter
        }
    }
}