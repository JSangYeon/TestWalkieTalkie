package jsy.sample.testwalkietalkie.utils

import jsy.sample.testwalkietalkie.application.MyApplication
import java.io.File

class PathUtils {
    companion object{
        val recordPath = File(MyApplication.instance.getExternalFilesDir(null)!!.absolutePath + "/recordFiles" )
//        val
    }
}