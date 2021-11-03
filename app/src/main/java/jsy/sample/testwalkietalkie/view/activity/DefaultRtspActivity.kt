package jsy.sample.testwalkietalkie.view.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.activity.viewModels
import com.pedro.encoder.input.video.CameraOpenException
import com.pedro.rtplibrary.rtsp.RtspCamera1
import com.pedro.rtsp.rtsp.VideoCodec
import com.pedro.rtsp.utils.ConnectCheckerRtsp
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ActivityDefaultRtspBinding
import jsy.sample.testwalkietalkie.utils.PathUtils
import jsy.sample.testwalkietalkie.view.base.BaseActivity
import jsy.sample.testwalkietalkie.view.model.RtspViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DefaultRtspActivity : BaseActivity<ActivityDefaultRtspBinding>(R.layout.activity_default_rtsp) {


    private val _rtspViewModel: RtspViewModel by viewModels()

    private lateinit var rtspCamera1: RtspCamera1

    private lateinit var context : Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            rtspViewModel = _rtspViewModel
            defaultRtspActivity = this@DefaultRtspActivity

            context = this@DefaultRtspActivity
            initView()

        }
    }

    private fun initView() {
        val surfaceView = binding.svDefaultRtspCamera
        rtspCamera1 = RtspCamera1(surfaceView, DefaultRtspConnectChecker())
        rtspCamera1.setReTries(10)
        rtspCamera1.setVideoCodec(VideoCodec.H265)

        _rtspViewModel.setRtspCamera1(rtspCamera1)
        surfaceView.holder.addCallback(
            DefaultRtspSurfaceHolderCallback()
        )

        _rtspViewModel.folder =  PathUtils.getRecordPath(this)

        _rtspViewModel.recordStarting.observe(binding.lifecycleOwner!!,{recording->
            when(recording) {
                true -> binding.btnStartRecord.text = getString(R.string.stop_record)
                false -> binding.btnStartRecord.text = getString(R.string.start_record)
            }
        })

        _rtspViewModel.streamingStatus.observe(binding.lifecycleOwner!!,{streaming->
            when(streaming) {
                true -> binding.btnStartStreaming.text = getString(R.string.stop_button)
                false -> binding.btnStartStreaming.text = getString(R.string.start_button)
            }
        })



    }

    fun switchCamera() {
        try {
            rtspCamera1.switchCamera()

        } catch (e: CameraOpenException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }




    inner class DefaultRtspSurfaceHolderCallback(
    ) : SurfaceHolder.Callback {
        override fun surfaceCreated(p0: SurfaceHolder) {
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            rtspCamera1.startPreview()
        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            if (rtspCamera1.isRecording) {
                _rtspViewModel.recordStop()
            Toast.makeText(
                context,
                "file " + _rtspViewModel.currentDateAndTime + ".mp4 saved in " + _rtspViewModel.folder?.getAbsolutePath(),
                Toast.LENGTH_SHORT
            ).show()
                _rtspViewModel.currentDateAndTime = ""
            }
            if (rtspCamera1.isStreaming) {
                _rtspViewModel.streamingStop()
            }

            rtspCamera1.stopPreview()
        }
    }


    inner class DefaultRtspConnectChecker() :
        ConnectCheckerRtsp {

        override fun onConnectionStartedRtsp(rtspUrl: String) {
        }

        override fun onConnectionSuccessRtsp() {

            runOnUiThread{
                Toast.makeText(
                    context,
                    "Connection success",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        override fun onConnectionFailedRtsp(reason: String) {
            if (rtspCamera1.reTry(5000, reason, null)) {
                runOnUiThread{
                    Toast.makeText(
                        context,
                        "Retry",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } else {

                runOnUiThread{
                    Toast.makeText(
                        context,
                        "Connection failed. $reason", Toast.LENGTH_SHORT
                    )
                        .show()
                }
                _rtspViewModel.streamingStop()
            }
        }

        override fun onNewBitrateRtsp(bitrate: Long) {
        }

        override fun onDisconnectRtsp() {

            runOnUiThread{
                Toast.makeText(
                    context,
                    "Disconnected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onAuthErrorRtsp() {
            runOnUiThread{
                Toast.makeText(
                    context,
                    "Auth error",
                    Toast.LENGTH_SHORT
                ).show()
            }

            _rtspViewModel.streamingStop()
        }

        override fun onAuthSuccessRtsp() {
            runOnUiThread{
                Toast.makeText(
                    context,
                    "Auth success",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }
}
