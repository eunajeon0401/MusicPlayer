package com.example.music_player

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.databinding.FragmentListBinding


class ListFragment : Fragment() {
    lateinit var binding : FragmentListBinding
    lateinit var mainContext : Context
    lateinit var listAdapter : MusicRecyclerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainContext = context
    }

    override fun onResume() {
        super.onResume()
        notifyItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater,container,false)
        notifyItem()
        return binding.root
    }

    fun notifyItem() {
        val dbHelper = DBHelper(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbHelper.selectMusicAll()

        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.listRecyclerView.layoutManager = linearLayoutManager
        listAdapter = MusicRecyclerAdapter(mainContext.applicationContext, this, musicList)
        binding.listRecyclerView.adapter = listAdapter
    }

    fun notifySearchItem(query: String) {
        val dbHelper = DBHelper(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbHelper.selectSearchMusic(query)
        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)

        binding.listRecyclerView.layoutManager = linearLayoutManager
        listAdapter = MusicRecyclerAdapter(mainContext.applicationContext, this, musicList)
        binding.listRecyclerView.adapter = listAdapter
    }
}