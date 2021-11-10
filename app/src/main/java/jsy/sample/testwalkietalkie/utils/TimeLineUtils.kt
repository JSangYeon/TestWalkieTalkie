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
        mediaTimeLineList.add(MediaTimeLine(cal, hourMinuteSimpleDateFormat().format(cal.time), timeCheck = true, startPoint = true, endPoint = true))
        return mediaTimeLineList
    }



    var minuteGap = ((cal.time.time - getDateStartTime().time)/60000).toInt()

    var currentDateRestMinute = ((cal.time.time/60000)%30).toInt()


    addMediaTimeLine(mediaTimeLineList, cal, timeCheck = true, startPoint = true, endPoint = false)

    if(minuteGap > currentDateRestMinute)
    {
        if(currentDateRestMinute > 0) //현재시각 ~ 30분 or 현재시각 ~ 00분
        {
            val restTime = currentDateRestMinute/10
            if(restTime>0)
            {
                cal.add(Calendar.MINUTE, -restTime)
                minuteGap -= restTime
                currentDateRestMinute -= restTime
                addMediaTimeLine(mediaTimeLineList, cal, timeCheck = false, startPoint = false, endPoint = false)
            }

            val index = currentDateRestMinute/10
            for(i in 0 until index){
                minuteGap -= 10
                cal.add(Calendar.MINUTE, -10)
                currentDateRestMinute -= 10
                addMediaTimeLine(mediaTimeLineList, cal, timeCheck = false, startPoint = false, endPoint = false)
            }
        }

        while(minuteGap>30){
            for(i in 0 until 3){
                minuteGap -= 10 // 10분씩 체크하기
                cal.add(Calendar.MINUTE, -10)
                addMediaTimeLine(mediaTimeLineList, cal, timeCheck = false, startPoint = false, endPoint = false)
            }

        }
    }

    if(minuteGap>0)
    {
        cal.add(Calendar.MINUTE,-minuteGap)
        addMediaTimeLine(mediaTimeLineList, cal, timeCheck = true, startPoint = false, endPoint = true)
    }

    return mediaTimeLineList
}




private fun addMediaTimeLine(mediaTimeLineList : ArrayList<MediaTimeLine>, cal:Calendar, timeCheck:Boolean, startPoint:Boolean, endPoint:Boolean ){

    mediaTimeLineList.add(MediaTimeLine(cal, hourMinuteSimpleDateFormat().format(cal.time), timeCheck, startPoint, endPoint))

}


private fun timeSimpleDateFormat():SimpleDateFormat =  SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

fun hourMinuteSimpleDateFormat():SimpleDateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())
