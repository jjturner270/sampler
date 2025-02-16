package com.example.myapplication

import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.firebase.fetchWavFiles
import com.example.myapplication.firebase.downloadFile
import com.example.myapplication.ui.showFileSelectionDialog

class MainActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private var loadedSounds = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(6).build()

        val playButton = findViewById<Button>(R.id.play_button)

        // Fetch available .wav files from Firebase Storage
        fetchWavFiles { fileNames ->
            if (fileNames.isNotEmpty()) {
                showFileSelectionDialog(this, fileNames) { selectedFile ->
                    downloadFile(this, selectedFile) { filePath ->
                        if (filePath != null) {
                            val soundId = soundPool.load(filePath, 1)
                            loadedSounds[selectedFile] = soundId
                        }
                    }
                }
            }
        }

        // Play first loaded sound when the button is clicked
        playButton.setOnClickListener {
            loadedSounds.values.firstOrNull()?.let { soundId ->
                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
