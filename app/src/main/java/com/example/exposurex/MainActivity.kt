package com.example.exposurex

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var scanQrButton: Button
    private lateinit var viewFinder: PreviewView
    private lateinit var captureButton: Button
    private lateinit var historyButton: Button
    private lateinit var workerIdLabel: TextView
    private lateinit var doseLabel: TextView
    private lateinit var doseValue: TextView
    private lateinit var doseGauge: ProgressBar
    private var imageCapture: ImageCapture? = null

    private lateinit var db: AppDatabase
    private var currentWorkerId: String = "Not Set"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
            }
        }

    private val qrScanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scannedId = result.data?.getStringExtra("WORKER_ID")
                if (scannedId != null) {
                    currentWorkerId = scannedId
                    workerIdLabel.text = "Worker ID: $currentWorkerId"
                    Toast.makeText(this, "Worker identified: $scannedId", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewFinder = findViewById(R.id.viewFinder)
        captureButton = findViewById(R.id.captureButton)
        historyButton = findViewById(R.id.historyButton)
        scanQrButton = findViewById(R.id.scanQrButton)
        workerIdLabel = findViewById(R.id.workerIdLabel)
        doseLabel = findViewById(R.id.doseLabel)
        doseValue = findViewById(R.id.doseValue)
        doseGauge = findViewById(R.id.doseGauge)

        db = AppDatabase.getDatabase(this)

        workerIdLabel.text = "Worker ID: $currentWorkerId"

        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        captureButton.setOnClickListener { takePhoto() }

        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        scanQrButton.setOnClickListener {
            qrScanLauncher.launch(Intent(this, QrScannerActivity::class.java))
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera failed to start", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        val photoFile = File(externalMediaDirs.firstOrNull(), fileName)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(this@MainActivity, "Capture failed", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    processImage(photoFile)
                }
            }
        )
    }

    private fun processImage(photoFile: File) {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val avgColor = DoseCalculator.getAverageColor(bitmap)
        val (label, value) = DoseCalculator.matchDose(avgColor)

        doseLabel.text = "Exposure Level: $label"
        doseValue.text = "Estimated Dose: $value%"
        doseGauge.progress = value

        val timestamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
            .format(System.currentTimeMillis())
        val record = ScanRecord(
            workerId = currentWorkerId,
            timestamp = timestamp,
            doseLevel = label,
            doseValue = value,
            isValid = true
        )

        lifecycleScope.launch {
            db.scanDao().insertScan(record)
        }

        Toast.makeText(this, "Scan complete: $label ($value%)", Toast.LENGTH_SHORT).show()
    }
}