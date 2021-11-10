package jsy.sample.testwalkietalkie.view.model

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jsy.sample.testwalkietalkie.data.MediaFile
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.utils.PathUtils
import jsy.sample.testwalkietalkie.utils.getMediaTimeLineList
import jsy.sample.testwalkietalkie.utils.getDateStartTime
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.collections.ArrayList

class MediaViewModel : ViewModel() {

    private val TAG = "MediaViewModel"

    private val _listFiles = MutableLiveData<ArrayList<MediaFile>?>()
    val listFiles : LiveData<ArrayList<MediaFile>?>
        get() = _listFiles

    private val _listMediaTimeLine = MutableLiveData<ArrayList<MediaTimeLine>>()
    val listMediaTimeLine : LiveData<ArrayList<MediaTimeLine>>
        get() = _listMediaTimeLine

    private val _currentFile = MutableLiveData<File?>()
    val currentFile : LiveData<File?>
        get() = _currentFile

    init {
        _listFiles.value = null
        _currentFile.value = null
    }

    fun getFolderFileList() {
        Log.d("getDateStartTime", "date : ${getDateStartTime()}")

        _listMediaTimeLine.value = getMediaTimeLineList()
    }

//    private fun getFileList(){
//        val recordFileDir = PathUtils.recordPath
//        Log.d(TAG, "Path : ${PathUtils.recordPath.name}")
//        if (!recordFileDir.isDirectory) return
//
//        val mediaTimeLineList = getMediaTimeLineList()
//
//        for(mediaTimeLine in mediaTimeLineList) {
//            val date = mediaTimeLine.date
//            val cal = Calendar.getInstance()
//            cal.time = date
//
//            if(cal.get(Calendar.MINUTE)%10==0) cal.add(Calendar.MINUTE,-10)
//
//            val mediaFileDir = File(
//                PathUtils.recordPath.absolutePath +"/" +
//                        cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DATE) +
//                    "/" + cal.get(Calendar.HOUR_OF_DAY)+"/"+ cal.get(Calendar.MINUTE)/10
//            )
//
//            if(!mediaFileDir.isDirectory) continue
//
//            when(mediaFileDir.listFiles()) {
//                null -> return
//                else -> {
//                    val mediaFileList = ArrayList<MediaFile>()
//                    for (mediaFile in mediaFileDir.listFiles()!!) {
//                        mediaFileList.add(MediaFile(mediaFile,Date( Files.readAttributes(mediaFile.toPath(),
//                            BasicFileAttributes::class.java).creationTime().toMillis()),
//                            getThumbnail(mediaFile)))
//                        Log.d(TAG, "mediaFile : ${mediaFile.name}")
//                    }
//                }
//            }
//        }



//        when(recordFileDir.listFiles())
//        {
//            null -> return
//            else -> {
//                for(dateDir in recordFileDir.listFiles()!!)// 날짜
//                {
//                    if(dateDir.isDirectory)
//                    {
//                        Log.d(TAG,"date absolutePath : ${dateDir.absolutePath}")
//                        for(hourDir in dateDir.listFiles()!!)
//                        {
//                            Log.d(TAG,"hourDir : ${hourDir.absolutePath}")
//
//                            for(minDir in hourDir.listFiles()!!)
//                            {
//                                Log.d(TAG,"minDir : ${minDir.absolutePath}")
//                            }
//                        } } } }
//        }


//        when(dir.listFiles())
//        {
//            null -> return
//            else -> {
//
////                val mediaFileList = ArrayList<MediaFile>();
////                _listFiles.value = dir.listFiles()!!
//
//                for(file in dir.listFiles()!!)
//                {
//                    val readAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
//                    Log.d(TAG, " aaaaaf : " + file.path.toString() + " , " + file.path.endsWith(".mp4") +", createTime : ${Date(readAttributes.creationTime().toMillis())}")
////                    mediaFileList.add(MediaFile(file,Date(readAttributes.creationTime().toMillis()),getThumbnail(file)))
//
//
//
//
//
//
//                }
//
////                _listFiles.value = mediaFileList
//            }
//        }
//    }

    fun setCurrentFile(file: File)
    {
        Log.d("setCurrentFile", "file : ${file}")
        _currentFile.value = file
    }



}