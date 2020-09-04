package com.example.audioplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class PlayerService : Service() {

    private val binder: android.os.Binder = Binder()
    private var mediaPlayer: MediaPlayer? = null
    private var pause: Boolean = false
    private var totalTime: Int = 0
    var onErrorListener =
        MediaPlayer.OnErrorListener { _, _, _ -> false }
        set(value) {
            field = value
            mediaPlayer?.setOnErrorListener(value)
        }
    var onPreparedListener = MediaPlayer.OnPreparedListener { }
        set(value) {
            field = value
            mediaPlayer?.setOnPreparedListener(value)
        }
    var onCompletionListener = MediaPlayer.OnCompletionListener { }
        set(value) {
            field = value
            mediaPlayer?.setOnCompletionListener(value)
        }
    val isPlaying
        get() = mediaPlayer?.isPlaying ?: false

    fun play(path: Uri) {
        mediaPlayer =
            MediaPlayer.create(applicationContext, path)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.5f, 0.5f)
        totalTime = mediaPlayer?.duration!!
        mediaPlayer?.start()

    }

    fun playPause() {
        if (isPlaying) {
            mediaPlayer?.pause()
            pause = true
            Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
        } else {
            mediaPlayer?.seekTo(mediaPlayer!!.currentPosition)
            mediaPlayer?.start()
            pause = false
            Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
        }
    }

    fun seekToBeginning() {
        TODO("Not yet implemented")
    }

    fun stop() {
        if (isPlaying || pause.equals(true)) {
            pause = false
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        }
    }



    override fun onBind(intent: Intent): IBinder = binder
    override fun onStartCommand(
        intent: Intent, flags: Int, startId: Int
    ): Int = START_STICKY

    inner class Binder : android.os.Binder() {
        val service: PlayerService get() = this@PlayerService
    }

}
