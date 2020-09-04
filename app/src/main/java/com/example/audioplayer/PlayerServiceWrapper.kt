package com.example.audioplayer

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.StringRes
import java.util.concurrent.atomic.AtomicReference

class PlayerServiceWrapper(
    private val context: Context,
    private val viewModel: AudioFilesViewModel,
    private val serviceHolder: AtomicReference<PlayerService?>
) {
    private val playerService: PlayerService?
        get() = serviceHolder.get()

    fun play(path: Uri) {
        playerService?.play(path)
        toast(R.string.mediaPlaying)
    }

    fun playPause() {
        playerService?.playPause()
    }

    fun stop() {
        playerService?.stop()
        toast(R.string.mediaStop)
    }

    fun next() {
        TODO("Not yet implemented")
    }

    fun previous() {
        TODO("Not yet implemented")
    }

    fun enqueue(songs: Collection<AudioFile>) {
        TODO("Not yet implemented")
    }

    private fun toast(@StringRes msg: Int) = Toast
        .makeText(context, context.getString(msg), Toast.LENGTH_LONG)
        .show()

    fun enqueue(song: AudioFile) = enqueue(listOf(song))
}
