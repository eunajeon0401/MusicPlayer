package com.example.music_player

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Parcelable
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player.databinding.ItemHolderBinding
import java.text.SimpleDateFormat

class LikeAdapter(val context: Context, val likeFragment: LikeFragment, val musicList: MutableList<Music>?) : RecyclerView.Adapter<LikeAdapter.CustomViewHolder>() {

    companion object{
        val ALBUM_SIZE = 50
    }

    override fun getItemCount(): Int {
        return musicList?.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = (holder as CustomViewHolder).binding
        val music = musicList?.get(position)

        binding.tvArtist.text = music?.artist
        binding.tvArtist.isSelected = true
        binding.tvTitle.text = music?.title
        binding.tvTitle.isSelected = true
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        val bitmap = context.let { music?.getAlbumlmage(it, ALBUM_SIZE) }
        Log.d("LogCheck", "${bitmap}")
        if (bitmap != null) {
            binding.ivAlbumArt.setImageBitmap(bitmap)
        } else {
            binding.ivAlbumArt.setImageResource(R.drawable.ic_play_24)
        }

        when(music?.likes) {
            0 -> binding.ivItemLike.setImageResource(R.drawable.ic_favorite_24)
            1 -> binding.ivItemLike.setImageResource(R.drawable.ic_heart24)
        }

        binding.ivItemLike.setOnClickListener {
            if(music?.likes == 0) {
                binding.ivItemLike.setImageResource(R.drawable.ic_favorite_24)
                music.likes = 1
            } else {
                binding.ivItemLike.setImageResource(R.drawable.ic_heart24)
                music?.likes = 0
            }

            if(music != null) {
                val dbHelper = DBHelper(context, MainActivity.DB_NAME, MainActivity.VERSION)
                val flag = dbHelper.updateLike(music)
                if (flag) {
                    likeFragment.notifyItem()
                } else {
                    Log.d("LogCheck", "MusicRecyclerAdapter.onBindViewHolder() ${music.id} UPDATE ERROR")
                }
            }
        }

        binding.root.setOnClickListener {
            val playList: ArrayList<Parcelable> = musicList as ArrayList<Parcelable>
            val intent = Intent(binding.root.context, PlaymusicActivity::class.java)
            intent.putExtra("playList", playList)
            intent.putExtra("position", position)
            intent.putExtra("music", music)
            binding.root.context.startActivity(intent)
        }
    }

    class CustomViewHolder(val binding : ItemHolderBinding) : RecyclerView.ViewHolder(binding.root)
}
