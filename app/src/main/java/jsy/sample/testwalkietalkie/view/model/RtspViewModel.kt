package jsy.sample.testwalkietalkie.view.model

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.encoder.input.video.CameraOpenException
import com.pedro.rtplibrary.rtsp.RtspCamera1
import jsy.sample.testwalkietalkie.messageEnum.ToastEnum
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class RtspViewModel : ViewModel() {

    private var LOG_TAG = "RtspViewModel"

    private var rtspCamera : RtspCamera1? = null

    var folder : File? = null
    set(value) {
        Log.d("파일테스트","setFolder")

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
        url.value = "rtsp://218.153.121.119:8554/jsy/test"
    }


    fun recordStart(temp: String){
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

    fun streamingStart(temp : String){
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
            if (!rtspCamera!!.isStreaming) {
                if (rtspCamera!!.prepareAudio() && rtspCamera!!.prepareVideo()
                ) {
                    recordStart(folder!!.absolutePath + "/" + currentDateAndTime + ".mp4")
                }
                else {
                    setToastEnum(ToastEnum.PlzPrepareStream)
                }
            } else {
                recordStart(folder!!.absolutePath + "/" + currentDateAndTime + ".mp4")
            }
        } else{
            recordStop()
        }

    }



    fun btnStreaming(){
        Log.d("btnStreaming","1")
        if (!rtspCamera!!.isStreaming) {
            Log.d("btnStreaming","2")
            if (rtspCamera!!.isRecording
                || rtspCamera!!.prepareAudio() && rtspCamera!!.prepareVideo()) {
                Log.d("btnStreaming","url : ${url.value}")
                streamingStart(url.value!!)
            } else {
                Log.d("btnStreaming","4")
                setToastEnum(ToastEnum.PlzPrepareStream)
            }
        } else {
            Log.d("btnStreaming","5")
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
        when(Looper.myLooper())
        {
            Looper.getMainLooper() -> _toastEnum.value = toastEnum
            else -> _toastEnum.postValue(toastEnum)
        }
    }



}