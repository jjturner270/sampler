package com.example.myapplication

import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private var kickSound = 0
    private var snareSound = 0
    private var hihatSound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SoundPool
        soundPool = SoundPool.Builder()
            .setMaxStreams(6) // Allows multiple sounds to play simultaneously
            .build()

        // Load sounds
        kickSound = soundPool.load(assets.openFd("sounds/kick.wav"), 1)
        snareSound = soundPool.load(assets.openFd("sounds/snare.wav"), 1)
        hihatSound = soundPool.load(assets.openFd("sounds/hihat.wav"), 1)

        // Assign sounds to buttons
        findViewById<Button>(R.id.kick_button).setOnClickListener {
            soundPool.play(kickSound, 1f, 1f, 0, 0, 1f)
        }

        findViewById<Button>(R.id.snare_button).setOnClickListener {
            soundPool.play(snareSound, 1f, 1f, 0, 0, 1f)
        }

        findViewById<Button>(R.id.hihat_button).setOnClickListener {
            soundPool.play(hihatSound, 1f, 1f, 0, 0, 1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
