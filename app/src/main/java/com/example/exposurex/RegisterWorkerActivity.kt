package com.example.exposurex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class RegisterWorkerActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_worker)

        db = AppDatabase.getDatabase(this)

        val nameInput: EditText = findViewById(R.id.workerNameInput)
        val generateButton: Button = findViewById(R.id.generateButton)
        val idLabel: TextView = findViewById(R.id.generatedIdLabel)
        val qrImageView: ImageView = findViewById(R.id.qrImageView)

        generateButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a worker name first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val workerId = "WKR-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
            val registeredOn = SimpleDateFormat("dd MMM yyyy", Locale.US)
                .format(System.currentTimeMillis())

            val worker = WorkerRecord(workerId = workerId, name = name, registeredOn = registeredOn)

            lifecycleScope.launch {
                db.workerDao().insertWorker(worker)
            }

            idLabel.text = "Worker ID: $workerId\nName: $name"

            val qrBitmap = QrCodeGenerator.generateQrBitmap(workerId)
            qrImageView.setImageBitmap(qrBitmap)

            Toast.makeText(this, "Worker registered!", Toast.LENGTH_SHORT).show()
        }
    }
}