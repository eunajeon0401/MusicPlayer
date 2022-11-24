package com.example.music_player

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper (context : Context, dbName : String, version :Int ) : SQLiteOpenHelper(context,dbName,null,version){

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            create table musicplayerTBL(
             id text primary key, 
             title text, 
             artist text, 
             albumId text, 
             duration integer, 
             likes integer)
            """.trimIndent()
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion : Int, oldVersion : Int) {
        val query ="""
            drop table musicplayerTBL
        """.trimIndent()
        db?.execSQL(query)
        this.onCreate(db)
    }

    fun selectMusicAll(): MutableList<Music>? {
        var musicList : MutableList<Music>? = mutableListOf<Music>()
        var cursor : Cursor? = null
        val query = """select * from musicplayerTBL""".trimIndent()
        val db = this.readableDatabase

        try{
            cursor = db.rawQuery(query,null)
            if(cursor.count > 0){
                while (cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title,artist,albumId,duration,likes)
                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e : Exception){
            Log.d("chap17mp3_dp1", "DBHelper selectMusicAll ${e.printStackTrace()}")
            musicList = null
        }finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun insertMusic(music: Music) : Boolean {
        var flag = false
        val query = """insert into musicplayerTBL(id, title, artist, albumId, duration, likes) 
            values('${music.id}','${music.title}','${music.artist}','${music.albumId}',${music.duration},${music.likes})
        """.trimIndent()
        val db = this.writableDatabase

        try{
            Log.d("chap17mp3_dp1", "DBHelper selectMusicAll ${query}")
            db.execSQL(query)
            flag = true
        }catch (e : Exception){
            Log.d("chap17mp3_dp1", "DBHelper selectMusicAll ${e.printStackTrace()}")
            flag = false
        }finally {
            db.close()
        }
        return flag
    }

    fun updateLike(music: Music): Boolean {
        var flag = false
        val query =
            """update musicplayerTBL set likes = ${music.likes} where id = '${music.id}'""".trimIndent()
        val db = this.writableDatabase

        try {
            db.execSQL(query)
            flag = true
        } catch (e: Exception) {
            Log.d("music_player", "class DBHelper.updateLike() ${e.printStackTrace()}")
            flag = false
        } finally {
            db.close()
        }
        return flag
    }

    fun selectSearchMusic(query: String): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            select * from musicplayerTBL where title like '%${query}%' OR artist like '%${query}%' 
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("LogCheck", "selectMusicAll() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectMusicClicked(type: String): MutableList<Music>?{
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            select * from musicplayerTBL where $type = 1
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("LogCheck", "DBHelper.selectMusicLike() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectSearchMusicClicked(query: String?, type: String) : MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            select * from musicplayerTBL where (title like '%${query}%' OR artist like '%${query}%') and $type = 1
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("LogCheck", "selectMusicAll() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }
}


