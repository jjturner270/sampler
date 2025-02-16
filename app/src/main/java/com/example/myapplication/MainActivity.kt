package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.SoundPool
import android.os.Bundle
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.os.Environment
import com.example.myapplication.firebase.fetchWavFiles
import com.example.myapplication.firebase.downloadFile

class MainActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private var loadedSounds = mutableMapOf<String, Int>() // Stores sound IDs
    private var activeStreamIds = mutableMapOf<String, Int>() // Stores currently playing sound stream IDs
    private lateinit var drumPadGrid: GridLayout
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var stopPreviousSwitch: Switch // Toggle switch for stopping sounds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(8).build()
        drumPadGrid = findViewById(R.id.drum_pad_grid)
        stopPreviousSwitch = findViewById(R.id.stop_previous_switch)

        val loadSoundsButton = findViewById<android.widget.Button>(R.id.load_sounds_button)

        loadSoundsButton.setOnClickListener {
            fetchWavFiles { fileNames ->
                if (fileNames.isNotEmpty()) {
                    loadSounds(fileNames)
                }
            }
        }
    }

    private fun loadSounds(fileNames: List<String>) {
        val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        // Delete existing files before downloading new ones
        musicDir?.listFiles()?.forEach { it.delete() }

        drumPadGrid.removeAllViews() // Clear previous buttons

        for (fileName in fileNames) {
            downloadFile(this, fileName) { filePath ->
                if (filePath != null) {
                    val soundId = soundPool.load(filePath, 1)
                    loadedSounds[fileName] = soundId

                    runOnUiThread {
                        createDrumPad(fileName, soundId)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createDrumPad(fileName: String, soundId: Int) {
        val button = android.widget.Button(this).apply {
            text = fileName.removeSuffix(".wav") // Show filename without .wav
            setBackgroundColor(Color.LTGRAY)
            setTextColor(Color.BLACK)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 250
                height = 250
                setMargins(10, 10, 10, 10)
            }

            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { // Pressing the button
                        if (stopPreviousSwitch.isChecked) { // If switch is ON, stop previous sound
                            activeStreamIds[fileName]?.let { soundPool.stop(it) }
                        }

                        val streamId = soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                        activeStreamIds[fileName] = streamId // Save the stream ID
                        setBackgroundColor(Color.DKGRAY) // Visual feedback
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { // Releasing the button
                        setBackgroundColor(Color.LTGRAY) // Restore button color
                    }
                }
                true
            }
        }

        drumPadGrid.addView(button)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
