package com.example.audioplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.databinding.AudioFileItemBinding

class AudioFileListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onClickListener: (Int) -> Unit,
    private val onClickListenerPlayStop: () -> Unit
): ListAdapter<AudioFile, AudioFileListAdapter.ViewHolder>(
    AudioFile.DIFF_CB
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = AudioFileItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val file = currentList[position]
        holder.bind(file, position)
    }
    //override fun getItemCount(): Int = fileList.size

    inner class ViewHolder(
        binding: AudioFileItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private val viewModel = MutableLiveData<AudioFile>()
        private val currentSong = MutableLiveData<AudioFile>()
        private var index = -1
        init {
            binding.audioFileVM = viewModel
            binding.lifecycleOwner = lifecycleOwner
            binding.listSong.setOnClickListener {
                if (index >= 0) onClickListener(index) }
            binding.playButton.setOnClickListener {
                onClickListenerPlayStop()
            }
        }
        fun bind(audioFile: AudioFile, position: Int) {
            viewModel.postValue(audioFile)
            this.index = position
        }
    }
}