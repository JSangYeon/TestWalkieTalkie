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
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.application.MyApplication
import jsy.sample.testwalkietalkie.data.MediaFile
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.utils.PathUtils
import jsy.sample.testwalkietalkie.utils.getTimeSharedPreferences
import jsy.sample.testwalkietalkie.utils.timeSimpleDateFormat
import jsy.sample.testwalkietalkie.view.recyclerView.MediaRecyclerView
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MediaViewModel : ViewModel() {

    private val TAG = "MediaViewModel"

    private val _listMediaTimeLine = MutableLiveData<ArrayList<MediaTimeLine>>()
    val listMediaTimeLine : LiveData<ArrayList<MediaTimeLine>>
        get() = _listMediaTimeLine

    private val _currentFile = MutableLiveData<File?>(null)
    val currentFile : LiveData<File?>
        get() = _currentFile

    private val _listOneMinutePixel = MutableLiveData<ArrayList<Int>>()
    val listOneMinutePixel : LiveData<ArrayList<Int>>
            get() = _listOneMinutePixel

    private val _currentScrolledTime = MutableLiveData<String>()
    val currentScrolledTime : LiveData<String>
        get() = _currentScrolledTime

    private val _currentInvisibleIndex = MutableLiveData<Int>(-1)
    val currentInvisibleIndex : LiveData<Int>
        get() = _currentInvisibleIndex

    private val _visibleIndex = MutableLiveData<Int>(-1)
    val visibleIndex : LiveData<Int>
        get() = _visibleIndex



    companion object{
        var firstMediaViewHeightCheck = true
        var viewHeight = 0
        var tvTimeHeight = 0
    }




    fun getFolderFileList() {
        Log.d("getDateStartTime", "date : ${getDateStartTime()}")
        _listMediaTimeLine.value = getMediaTimeLineList()
    }

    fun setCurrentFile(file: File)
    {
        Log.d("setCurrentFile", "file : ${file}")
        _currentFile.value = file
    }


    fun setOneMinutePixelList(){
        val oneMinutePixelList = ArrayList<Int>()
        val cal = Calendar.getInstance()
        for(mediaTimeLine in _listMediaTimeLine.value!!) {
            cal.time = mediaTimeLine.date
            val restTime = cal.get(Calendar.MINUTE)%10

            if(restTime==0) // 10분단위
                oneMinutePixelList.add(viewHeight/10)
            else
                oneMinutePixelList.add(viewHeight/restTime)
        }

        _listOneMinutePixel.value = oneMinutePixelList
    }


    fun getScrollTime(scrollPosition : Int, scrollHeight : Int){ //스크롤된 위치의 시간 구하기

        val position = scrollHeight / viewHeight
        val height = scrollHeight % viewHeight
        Log.d("position","${position}, scrollPosition ${scrollPosition}, scrollHeight ${scrollHeight}")

        val minute = height/listOneMinutePixel.value!![scrollPosition]


        getCheck(scrollPosition, height)

        val cal = Calendar.getInstance()
        cal.time = listMediaTimeLine.value!![scrollPosition].date


//        val positionMinute = cal.get(Calendar.MINUTE)
//
        cal.add(Calendar.MINUTE, -minute)
//        val scrolledMinute = cal.get(Calendar.MINUTE)

//        Log.d("minute비교", "position : ${positionMinute}, scrolled : ${scrolledMinute}")
        _currentScrolledTime.value = hourMinuteSimpleDateFormat().format(cal.time)




//        Log.d("getScrollTime", "time : ${_currentScrolledTime.value}, scrollPosition : ${scrollPosition}")

    }


    private fun getCheck(scrollPosition : Int, height : Int)
    {
        if(height< tvTimeHeight) {
            if(_currentInvisibleIndex.value != scrollPosition) _currentInvisibleIndex.value = scrollPosition
        } else if ((height > viewHeight-tvTimeHeight)){
            if(_currentInvisibleIndex.value != scrollPosition+1 && scrollPosition+1 <  listMediaTimeLine.value!!.size )
            _currentInvisibleIndex.value = scrollPosition+1
        }else if(_visibleIndex.value != _currentInvisibleIndex.value){
            _visibleIndex.value = _currentInvisibleIndex.value
            _currentInvisibleIndex.value = -1
        }
    }








    private fun getDateStartTime() : Date{
        val startTime = getTimeSharedPreferences().getString(MyApplication.instance.getString(R.string.start_time), null)
        Log.d("getDateStartTime", "startTime : $startTime")

        return timeSimpleDateFormat().parse(startTime!!)!!
    }


    private fun getMediaTimeLineList() : java.util.ArrayList<MediaTimeLine> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.SECOND, 0)
        val mediaTimeLineList = java.util.ArrayList<MediaTimeLine>()

        if(cal.time.time == getDateStartTime().time) // 앱 처음킨 시간에 들어왔을 때
        {
            addMediaTimeLine(mediaTimeLineList, cal, startPoint = true, endPoint = true) // 현재시간
            return mediaTimeLineList
        }

        var minuteGap = ((cal.time.time - getDateStartTime().time)/60000).toInt() // 시작시간, 현재시간의 분 차이

        var currentDateRestMinute = ((cal.time.time/60000)%10).toInt() // 앞의 짜투리시간 제거 05분~ 07분 등

        addMediaTimeLine(mediaTimeLineList, cal, startPoint = true, endPoint = false) // 현재시간

        if(minuteGap > currentDateRestMinute)
        {
            if(currentDateRestMinute > 0) //가까운 10분대 단위 맞추기
            {
                cal.add(Calendar.MINUTE, -currentDateRestMinute)
                minuteGap -= currentDateRestMinute
                currentDateRestMinute -= currentDateRestMinute
                addMediaTimeLine(mediaTimeLineList, cal, startPoint = false, endPoint = false)
            }

            while(minuteGap>10){// 10분씩 체크
                minuteGap -= 10
                cal.add(Calendar.MINUTE, -10)
                addMediaTimeLine(mediaTimeLineList, cal, startPoint = false, endPoint = false)
            }
        }

//    if(minuteGap>0) // 시작시간
//    {
//        cal.add(Calendar.MINUTE,-minuteGap)
//        addMediaTimeLine(mediaTimeLineList, cal, startPoint = false, endPoint = true) // 시작시간
//    }

        return mediaTimeLineList
    }



    private fun addMediaTimeLine(mediaTimeLineList : java.util.ArrayList<MediaTimeLine>, cal:Calendar, startPoint:Boolean, endPoint:Boolean ){

        var timeCheck = false;

        val date = cal.time

        if(!startPoint && cal.get(Calendar.MINUTE)%30 == 0) timeCheck = true

        mediaTimeLineList.add(MediaTimeLine(date, hourMinuteSimpleDateFormat().format(cal.time), startPoint, endPoint, timeCheck, getMediaAdapter(date)))

    }



    private fun hourMinuteSimpleDateFormat(): SimpleDateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())



    private fun getMediaAdapter(date:Date) : MediaRecyclerView.MediaAdapter?{
        val recordFileDir = PathUtils.recordPath
        if (!recordFileDir.isDirectory) return null


        val cal = Calendar.getInstance()
        cal.time = date

        if(cal.get(Calendar.MINUTE)%10==0) cal.add(Calendar.MINUTE,-10)

        val mediaFileDir = File(
            PathUtils.recordPath.absolutePath +"/" +
                    cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DATE) +
                    "/" + cal.get(Calendar.HOUR_OF_DAY)+"/"+ cal.get(Calendar.MINUTE)/10
        )

        if(!mediaFileDir.isDirectory) return null


        Log.d("MediaFileLoop", "mediaFileDir : ${mediaFileDir.absolutePath}")
        return when(mediaFileDir.listFiles()) {
            null -> null
            else -> {

                val mediaFileList = java.util.ArrayList<MediaFile>()
                for (mediaFile in mediaFileDir.listFiles()!!) {
                    Log.d("MediaFileLoop", "mediaFile : ${mediaFile.name}")
                    mediaFileList.add(
                        MediaFile(mediaFile,Date( Files.readAttributes(mediaFile.toPath(),
                            BasicFileAttributes::class.java).creationTime().toMillis()),
                            getThumbnail(mediaFile))
                    )
                }

                val adapter = MediaRecyclerView.MediaAdapter(this@MediaViewModel)
                adapter.replaceAll(mediaFileList)
                return adapter


            }
        }
    }




    private fun getThumbnail(file: File) : Bitmap? {

        val size = Size(100,100)
        val cancellationSignal = CancellationSignal()

        val bitmap = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q)
        {
            ThumbnailUtils.createVideoThumbnail(file, size, cancellationSignal);
        } else{
            ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        }

        val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 120, 160);

        return thumbnail
    }

}