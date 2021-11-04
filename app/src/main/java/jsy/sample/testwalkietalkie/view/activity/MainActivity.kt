package jsy.sample.testwalkietalkie.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.pedro.rtplibrary.rtsp.RtspCamera1
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ActivityMainBinding
import jsy.sample.testwalkietalkie.view.base.BaseActivity
import jsy.sample.testwalkietalkie.view.model.RtspViewModel

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun ActivityMainBinding.init() {
        ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        mainActivity = this@MainActivity
    }

    fun btnDefaultRtsp() {
        if(checkPermissions()) {
            val intent = Intent(this, DefaultRtspActivity::class.java)
            startActivity(intent)
        }

    }

    fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }



}
