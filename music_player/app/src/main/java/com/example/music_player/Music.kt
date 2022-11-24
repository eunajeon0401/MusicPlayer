package com.example.music_player

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.io.IOException

@Parcelize
class Music (var id:String, var title:String?, var artist:String?, var albumId: String?, var duration: Int?, var likes : Int?
) : Parcelable {
    //serializable로 안하고 parcelable 하는 이유는 속도처리, 용량처리가 더 좋음
    companion object : Parceler<Music> {
        override fun create(parcel: Parcel): Music {
            return Music(parcel)
        }

        //parcelable 쓸때
        override fun Music.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(artist)
            parcel.writeString(albumId)
            parcel.writeInt(duration!!)
            parcel.writeInt(likes!!)
        }
    }

    //parcelable로 읽을 때
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun getAlbumUri(): Uri{
        return Uri.parse("content://media/external/audio/albumart/"+albumId)
    }

    fun getMusicUri() : Uri{
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)
    }

    fun getAlbumlmage(context : Context, albumlmageSize : Int) :Bitmap?{
        val contentResolver : ContentResolver = context.contentResolver
        var uri = getAlbumUri()
        var option = BitmapFactory.Options()

        if(uri != null){
            var parcelFileDescriptor : ParcelFileDescriptor? = null
            try{
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri,"r")
                var bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor!!.fileDescriptor,null,option)
                if (bitmap != null){
                    val tempBitmap = Bitmap.createScaledBitmap(bitmap,albumlmageSize,albumlmageSize,true)
                    bitmap.recycle()
                    bitmap = tempBitmap
                }
                return bitmap
            }catch (e : Exception){
                Log.d("music_player","getAlbumImage() ${e.toString()}")
            }finally {
                try{
                    parcelFileDescriptor?.close()
                }catch (e : IOException){
                    Log.d("music_player","getAlbumImage() parcelFileDescriptor ${e.toString()}")
                }
            }
        }
        return null
    }
}