package com.example.audioplayer

import android.net.Uri
import java.util.*

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int = 1337,
    val filePath: Uri = Uri.EMPTY
) {
    val durationText: String
        get() {
            val s = duration % 60
            val durationMinute = (duration - s) / 60
            val m = durationMinute % 60
            val h = (durationMinute - m) / 60
            val l = Locale.getDefault()
            return if(h > 0) "%02d:%02d:%02d".format(l, h, m, s)
            else "%02d:%02d".format(l, m, s)
        }
}