package jsy.sample.testwalkietalkie.view.model

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.encoder.input.video.CameraOpenException
import com.pedro.rtplibrary.rtsp.RtspCamera1
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.application.MyApplication
import jsy.sample.testwalkietalkie.messageEnum.ToastEnum
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class RtspViewModel : ViewModel() {

    private var LOG_TAG = "RtspViewModel"

    private var rtspCamera : RtspCamera1? = null

    var folder : File? = null
    set(value) {
        field = value
        if (!field!!.exists()) {
            field!!.mkdir()
        }
    }

    var customMessage : String? = null
    set(value) {
        field = value
        setToastEnum(ToastEnum.CustomMessage)
    }


    var currentDateAndTime = "";

    val url = MutableLiveData<String>()

    private val _recordStatus = MutableLiveData<Boolean>()
    val recordStarting : LiveData<Boolean>
        get() = _recordStatus

    private val _streamingStatus = MutableLiveData<Boolean>()
    val streamingStatus : LiveData<Boolean>
        get() = _streamingStatus

    private val _toastEnum = MutableLiveData<ToastEnum>()
    val toastEnum : LiveData<ToastEnum>
        get() = _toastEnum


    init {
        _recordStatus.value = false
        _streamingStatus.value = false
        setToastEnum(ToastEnum.None)
        url.value = MyApplication.instance.getString(R.string.rtsp_url)
    }


    private fun recordStart(temp: String){
        val file = File(temp)
        val fos = FileOutputStream(file)
        val fileDescriptor  = fos.fd

        rtspCamera?.startRecord(fileDescriptor)
        _recordStatus.value = true
    }

    fun recordStop(){
        rtspCamera?.stopRecord()
        _recordStatus.value = false
    }

    private fun streamingStart(temp : String){
        Log.d(LOG_TAG, "streamingStart : ${rtspCamera==null}")
        rtspCamera?.startStream(temp)
        _streamingStatus.value = true
    }

    fun streamingStop(){
        rtspCamera?.stopStream()
        _streamingStatus.value = false
    }


    fun setRtspCamera1(rtspCamera: RtspCamera1){
        this.rtspCamera = rtspCamera
    }



    fun btnRecord(){

        if(!rtspCamera!!.isRecording)
        {
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

            currentDateAndTime = sdf.format(Date())

            val sb = StringBuilder()
            sb.append(folder!!.absolutePath)
            sb.append("/")
            sb.append(currentDateAndTime)
            sb.append(".mp4")

            if (!rtspCamera!!.isStreaming) {
                if (rtspCamera!!.prepareAudio() && rtspCamera!!.prepareVideo()
                ) {
                    recordStart(sb.toString())
                }
                else {
                    setToastEnum(ToastEnum.PlzPrepareStream)
                }
            } else {
                recordStart(sb.toString())
            }
        } else{
            recordStop()
        }

    }



    fun btnStreaming(){
        if (!rtspCamera!!.isStreaming) {
            if (rtspCamera!!.isRecording
                || rtspCamera!!.prepareAudio() && rtspCamera!!.prepareVideo()) {
                streamingStart(url.value!!)
            } else {
                setToastEnum(ToastEnum.PlzPrepareStream)
            }
        } else {
            streamingStop()
        }

    }

    fun switchCamera() {
        try {
            rtspCamera!!.switchCamera()

        } catch (e: CameraOpenException) {
            customMessage = e.message.toString()
        }
    }


    fun setToastEnum(toastEnum: ToastEnum){
        if(Looper.getMainLooper().isCurrentThread) _toastEnum.value = toastEnum
        else  _toastEnum.postValue(toastEnum)
    }



}