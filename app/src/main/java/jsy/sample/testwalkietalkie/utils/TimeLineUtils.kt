package jsy.sample.testwalkietalkie.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.application.MyApplication
import jsy.sample.testwalkietalkie.data.MediaFile
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.view.recyclerView.MediaRecyclerView
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*

fun setStartTime(){

    val sharedPreferences = getTimeSharedPreferences()
    val startTime = sharedPreferences.getString(MyApplication.instance.getString(R.string.start_time), null)
    Log.d("StartTimeCheck","startTime : $startTime")

    if(startTime == null)
    {
        Log.d("StartTimeCheck","set startTime : $startTime")
        val sdf = timeSimpleDateFormat()
        with(sharedPreferences.edit()){
            putString( jsy.sample.testwalkietalkie.application.MyApplication.instance.getString(jsy.sample.testwalkietalkie.R.string.start_time),sdf.format(
                Date()
            ))
            commit()
        }
    }

}

fun timeSimpleDateFormat(): SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

fun getTimeSharedPreferences() : SharedPreferences =
    MyApplication.instance.getSharedPreferences(MyApplication.instance.getString(R.string.time_shared_preference), Context.MODE_PRIVATE)


