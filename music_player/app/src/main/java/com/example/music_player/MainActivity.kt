package com.example.music_player

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player.databinding.ActivityMainBinding
import com.example.music_player.databinding.UsertabButtonBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    companion object{
        val REQ_READ = 99
        val DB_NAME ="musicplayerDB"
        var VERSION = 1
    }

    lateinit var binding : ActivityMainBinding
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var listFragment : ListFragment
    lateinit var likeFragment: LikeFragment
    private var musicList : MutableList<Music>? = mutableListOf<Music>()
    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQ_READ)
        }

        val pagerAdapter = PagerAdapter(this)
        val title = mutableListOf<String>("List","like")
        listFragment = ListFragment()
        likeFragment = LikeFragment()

        pagerAdapter.addFragment(listFragment,title[0])
        pagerAdapter.addFragment(likeFragment,title[1])

        binding.viewpager.adapter = pagerAdapter

        TabLayoutMediator(binding.tablayout, binding.viewpager){tab, position->
            tab.setCustomView(cueateTabView(title[position]))
        }.attach()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_READ && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startProcess()
        } else {
            Toast.makeText(this, "권한이 없어 프로그램이 종료됩니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun isPermitted(): Boolean {
        return ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
    }

    fun startProcess() {
        val dbHelper = DBHelper(this, DB_NAME, VERSION)
        musicList = dbHelper.selectMusicAll()

        if (musicList == null) {
            val playMusicList = getMusicList()
            if (playMusicList != null) {
                for(i in 0 .. playMusicList.size - 1) {
                    val music = playMusicList.get(i)
                    dbHelper.insertMusic(music)
                }
                musicList = playMusicList
            } else {
                Log.d("LogCheck", "MainActivity.startProcess() 외장메모리에 음악없음")
            }
        }
    }

    fun getMusicList(): MutableList<Music>? {
        var getMusicList: MutableList<Music>? = mutableListOf<Music>()
        val musicURL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = contentResolver.query(musicURL, projection, null, null, null)
        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1).replace("'", "")
                val artist = cursor.getString(2).replace("'", "")
                val albumId = cursor.getString(3)
                val duration = cursor.getInt(4)
                val music = Music(id, title, artist, albumId, duration, 0)
                getMusicList?.add(music)
            }
        } else {
            getMusicList = null
        }
        return getMusicList
    }


    fun cueateTabView(title : String) : View {
        val userTabBinding = UsertabButtonBinding.inflate(layoutInflater)
        userTabBinding.tvLike.text = title
        when(title){
            "List" ->userTabBinding.ivLike.setImageResource(R.drawable.ic_list_24)
            "Like" ->userTabBinding.ivLike.setImageResource(R.drawable.ic_heart24)
        }
        return userTabBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navi, menu)
        val dbHelper = DBHelper(applicationContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val searchMenu = menu?.findItem(R.id.menuSearch)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(query.isNullOrBlank()) {
                    Log.d("LogCheck", "currentItem ${binding.viewpager.currentItem}")
                    musicList?.clear()
                    when(binding.viewpager.currentItem) {
                        0 -> listFragment.notifyItem()
                        1 -> likeFragment.notifyItem()
                    }
                } else {
                    musicList?.clear()
                    Log.d("LogCheck", "currentItem ${binding.viewpager.currentItem}")
                    when(binding.viewpager.currentItem) {
                        0 -> listFragment.notifySearchItem(query)
                        1 -> likeFragment.notifySearchItem(query)
                    }
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

}