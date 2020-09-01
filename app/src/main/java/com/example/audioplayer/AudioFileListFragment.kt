package com.example.audioplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

class AudioFileListFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAudioFileListBinding.inflate(
            inflater,
            container,
            false
        )
        binding.audioFileList.layoutManager =
            LinearLayoutManager(binding.root.context)
        return binding.root
    }
}

