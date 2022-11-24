package com.example.music_player

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.databinding.FragmentLikeBinding
import com.example.music_player.databinding.FragmentListBinding

class LikeFragment : Fragment() {
    lateinit var binding : FragmentLikeBinding
    lateinit var likeAdapter: LikeAdapter
    lateinit var mainContext : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainContext = context
    }

    override fun onResume() {
        super.onResume()
        notifyItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentLikeBinding.inflate(inflater,container,false)
        notifyItem()
        return binding.root
    }

    fun notifyItem() {
        val dbHelper = DBHelper(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbHelper.selectMusicClicked("likes")
        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.likeRecyclerView.layoutManager = linearLayoutManager
        likeAdapter = LikeAdapter(mainContext.applicationContext, this, musicList)
        binding.likeRecyclerView.adapter = likeAdapter
    }


    fun notifySearchItem(query: String?) {
        val dbHelper =DBHelper(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbHelper.selectSearchMusicClicked(query, "likes")
        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.likeRecyclerView.layoutManager = linearLayoutManager
        likeAdapter = LikeAdapter(mainContext.applicationContext, this, musicList)
        binding.likeRecyclerView.adapter = likeAdapter
    }

}