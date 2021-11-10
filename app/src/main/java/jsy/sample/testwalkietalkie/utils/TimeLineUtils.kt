package jsy.sample.testwalkietalkie.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.application.MyApplication
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import java.text.SimpleDateFormat
import java.util.*


private fun getTimeSharedPreferences() : SharedPreferences =
    MyApplication.instance.getSharedPreferences(MyApplication.instance.getString(R.string.time_shared_preference), Context.MODE_PRIVATE)


fun setStartTime(){

    val sharedPreferences = getTimeSharedPreferences()
    val startTime = sharedPreferences.getString(MyApplication.instance.getString(R.string.start_time), null)
    Log.d("StartTimeCheck","startTime : $startTime")

    if(startTime == null)
    {
        Log.d("StartTimeCheck","set startTime : $startTime")
        val sdf = timeSimpleDateFormat()
        with(sharedPreferences.edit()){
            putString( MyApplication.instance.getString(R.string.start_time),sdf.format(Date()))
            commit()
        }
    }

}


fun getDateStartTime() : Date{
    val startTime = getTimeSharedPreferences().getString(MyApplication.instance.getString(R.string.start_time), null)
    Log.d("getDateStartTime", "startTime : $startTime")

    return timeSimpleDateFormat().parse(startTime!!)!!
}


fun getMediaTimeLineList() : ArrayList<MediaTimeLine> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.SECOND, 0)
    val mediaTimeLineList = ArrayList<MediaTimeLine>()

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

    if(minuteGap>0) // 시작시간
    {
        cal.add(Calendar.MINUTE,-minuteGap)
        addMediaTimeLine(mediaTimeLineList, cal, startPoint = false, endPoint = true) // 시작시간
    }

    return mediaTimeLineList
}




private fun addMediaTimeLine(mediaTimeLineList : ArrayList<MediaTimeLine>, cal:Calendar, startPoint:Boolean, endPoint:Boolean ){

    var timeCheck = false;

    if(startPoint||endPoint||cal.get(Calendar.MINUTE)%30 == 0) timeCheck = true

    mediaTimeLineList.add(MediaTimeLine(cal, hourMinuteSimpleDateFormat().format(cal.time), startPoint, endPoint, timeCheck))

}


private fun timeSimpleDateFormat():SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

fun hourMinuteSimpleDateFormat():SimpleDateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())
