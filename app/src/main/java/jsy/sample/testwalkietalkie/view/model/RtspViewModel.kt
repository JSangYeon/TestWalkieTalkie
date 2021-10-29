package jsy.sample.testwalkietalkie.view.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.encoder.input.video.CameraOpenException
import com.pedro.rtplibrary.rtsp.RtspCamera1


class RtspViewModel : ViewModel() {

    private var LOG_TAG = "RtspViewModel"

    var rtspCamera : RtspCamera1? = null

    private val _recordStatus = MutableLiveData<Boolean>()
    val recordStarting : LiveData<Boolean>
        get() = _recordStatus

    private val _streamingStatus = MutableLiveData<Boolean>()
    val streamingStatus : LiveData<Boolean>
        get() = _streamingStatus


    init {
        _recordStatus.value = false
        _streamingStatus.value = false

    }

    fun isRecording() : Boolean = _recordStatus.value!!

    fun recordStart(temp: String){
        rtspCamera?.startRecord(temp)
        _recordStatus.value = true
    }

    fun recordStop(){

        rtspCamera?.stopRecord()
        _recordStatus.value = false
    }

    fun isStreaming() : Boolean = _streamingStatus.value!!

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



}