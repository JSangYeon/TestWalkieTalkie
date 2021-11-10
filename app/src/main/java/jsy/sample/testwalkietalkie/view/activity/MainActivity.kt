package jsy.sample.testwalkietalkie.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ActivityMainBinding
import jsy.sample.testwalkietalkie.utils.setStartTime
import jsy.sample.testwalkietalkie.view.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun ActivityMainBinding.init() {
        ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        mainActivity = this@MainActivity

        setStartTime()
    }



    fun btnDefaultRtsp() {
        if (checkPermissions()) {
            startActivity(Intent(this, DefaultRtspActivity::class.java))
        }
    }

    fun btnMedia() {
        startActivity(Intent(this, MediaActivity::class.java))
    }


    private fun checkPermissions(): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            }
        }

        return true;
    }


}
