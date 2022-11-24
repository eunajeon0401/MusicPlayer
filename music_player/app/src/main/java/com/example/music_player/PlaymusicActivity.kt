package com.example.music_player

import android.media.MediaPlayer
import android.os.Build.VERSION_CODES.S
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.widget.SeekBar
import com.example.music_player.MusicRecyclerAdapter.Companion.ALBUM_SIZE
import com.example.music_player.databinding.ActivityPlaymusicBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlaymusicActivity : AppCompatActivity() {

    companion object {
        val ALBUM_SIZE = 380
        val PREVIOUS = 0
        val NEXT = 1
    }

    private lateinit var binding: ActivityPlaymusicBinding
    private var playList: MutableList<Parcelable>? = null
    private var position: Int = 0
    private var music: Music? = null
    private var mediaPlayer: MediaPlayer? = null
    private var messengerJob: Job? = null
    private val PREVIOUS = 0
    private val NEXT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaymusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트 정보 가져오기
        playList = intent.getParcelableArrayListExtra("playList")
        position = intent.getIntExtra("position", 0)
        music = playList?.get(position) as Music

        stratMusic(music)

        //이벤트 설정 목록버튼
        binding.btnList.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()
            finish()
        }
        //이벤트 설정 정지버튼
        binding.btnStop.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()
            mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
            binding.seekBar.progress = 0
            binding.playDuration.text = "00:00"
        }
        //이벤트 설정 재생버튼
        binding.btnPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.btnPlay.setImageResource(R.drawable.ic_play2_24)
            } else {
                mediaPlayer?.start()
                binding.btnPlay.setImageResource(R.drawable.ic_stop2_24)

                val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                messengerJob = backgroundScope.launch {
                    while (mediaPlayer?.isPlaying == true) {

                        runOnUiThread {
                            var currentPosition = mediaPlayer?.currentPosition!!
                            binding.seekBar.progress = currentPosition
                            val currentDurateion =
                                SimpleDateFormat("mm:ss").format(mediaPlayer!!.currentPosition)
                            binding.playDuration.text = currentDurateion
                        }
                        try {
                            // 1초마다 수행되도록 딜레이
                            delay(1000)
                        } catch (e: Exception) {
                            Log.d("로그", "스레드 오류 발생")
                        }
                    }//end of while
                    runOnUiThread {
                        if (mediaPlayer!!.currentPosition >= (binding.seekBar.max - 1000)) {
                            binding.seekBar.progress = 0
                            binding.playDuration.text = "00:00"
                        }
                        binding.btnPlay.setImageResource(R.drawable.ic_play2_24)
                    }
                }//end of messengerJob
            }
        }//end of playButton

        binding.btnPrevious.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()

            position = getPosition(PREVIOUS, position)
            music = playList?.get(position) as Music
            stratMusic(music)
        }

        binding.btnNext.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()

            position = getPosition(NEXT, position)
            music = playList?.get(position) as Music
            stratMusic(music)
        }
    }
    fun getPosition(option:Int , position : Int) :Int {
        var newPosition: Int = position
        when (position) {
            0 -> {
                newPosition = if(option == PREVIOUS) playList!!.size -1 else position +1
            }
            in 1 until (playList!!.size - 1) -> {
                newPosition = if (option == PREVIOUS) position -1 else position +1
            }
            playList!!.size -1 -> {
                newPosition = if (option == PREVIOUS) position - 1 else 0
            }
        }
        return newPosition
    }

    fun stratMusic(music: Music?){
        binding.albumTitle.text = music?.title
        binding.albumArtist.text = music?.artist
        binding.albumTitle.isSingleLine = true
        binding.albumTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.albumTitle.isSelected = true
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        binding.playDuration.text = "00:00"
        val bitmap = music?.getAlbumlmage(this, ALBUM_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.ic_music_24)
        }

        //음악 등록
        mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())

        //시크바 음악 재생위치 변경
        binding.seekBar.max = mediaPlayer!!.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("chap17mp3_dp1", "움직인다")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.d("chap17mp3_dp1", "안움직인다?")
            }
        })
    }
}
