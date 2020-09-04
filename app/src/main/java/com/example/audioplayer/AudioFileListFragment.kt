package com.example.audioplayer

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioplayer.databinding.FragmentAudioFileListBinding
import kotlinx.android.synthetic.main.audio_file_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AudioFileListFragment : Fragment() {
    private val viewModel by
    activityViewModels<AudioFilesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.audioFiles.postValue(listOf())

        val binding = FragmentAudioFileListBinding.inflate(
            inflater,
            container,
            false
        )

        binding.audioFileList.layoutManager =
            LinearLayoutManager(binding.root.context)

        val activity = (requireActivity()as MainActivity)

        val adapter = AudioFileListAdapter(viewLifecycleOwner,  {
            activity.playerService.play(viewModel.audioFiles.value!![it])
            //activity.playerService.stop()
        }) {
            activity.playerService.playPause()
        }


        viewModel.audioFiles
            .observe(viewLifecycleOwner, adapter::submitList)
        binding.audioFileList.adapter = adapter

        querySongs()
        return binding.root
    }

    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION
    )

    fun querySongs() = GlobalScope.launch(Dispatchers.IO) {
        val contentResolver = requireActivity().contentResolver
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        val audioList = mutableListOf<AudioFile>()
        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getInt(durationColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                audioList += AudioFile(id, title, artist, album, duration, contentUri)
            }
        }
        viewModel.audioFiles.postValue(audioList)
    }
}

