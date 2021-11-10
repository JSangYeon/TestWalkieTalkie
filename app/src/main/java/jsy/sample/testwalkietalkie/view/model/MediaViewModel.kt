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
import jsy.sample.testwalkietalkie.utils.getMediaTimeLineList
import jsy.sample.testwalkietalkie.utils.getDateStartTime
import java.io.File

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

//        val calList = getCalList()

//        val mediaTimeLineList = ArrayList<MediaTimeLine>()
//
//        for(i in calList.indices) { //cal의 시간 분 범위안에 드는 파일들 다 넣어줘야함
//            val startPoint = i==0
//            val endPoint = i==calList.size-1
//            mediaTimeLineList.add(MediaTimeLine(calList[i],hourMinuteSimpleDateFormat().format(calList[i].time),startPoint,endPoint))
//        }

        _listMediaTimeLine.value = getMediaTimeLineList()

//        val dir = PathUtils.recordPath
//
//
//        if (!dir.isDirectory) {
//            if (!dir.mkdirs()) {
//                Log.d(TAG, " aaaaa")
//            }
//        }
//
//        when(dir.listFiles())
//        {
//            null -> return
//            else -> {
//
//                val mediaFileList = ArrayList<MediaFile>();
////                _listFiles.value = dir.listFiles()!!
//
//                for(file in dir.listFiles()!!)
//                {
//                    Log.d(TAG, " aaaaaf : " + file.path.toString() + " , " + file.path.endsWith(".mp4"))
//                    mediaFileList.add(MediaFile(file,getThumbnail(file)))
//                }
//
//                _listFiles.value = mediaFileList
//            }
//        }
    }

    fun setCurrentFile(file: File)
    {
        Log.d("setCurrentFile", "file : ${file}")
        _currentFile.value = file
    }


    private fun getThumbnail(file: File) : Bitmap{

        val size = Size(100,100)
        val cancellationSignal = CancellationSignal()
        var bitmap : Bitmap? = null
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q)
        {
            bitmap = ThumbnailUtils.createVideoThumbnail(file, size, cancellationSignal);
        } else{
            bitmap = ThumbnailUtils.createVideoThumbnail(file.path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        }

        val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 360, 480);

        return thumbnail
    }


}