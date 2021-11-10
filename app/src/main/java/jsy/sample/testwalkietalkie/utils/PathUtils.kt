package jsy.sample.testwalkietalkie.utils

import jsy.sample.testwalkietalkie.application.MyApplication
import java.io.File
import java.util.*

class PathUtils {
    companion object{
        val recordPath = File(MyApplication.instance.getExternalFilesDir(null)!!.absolutePath + "/recordFiles")

        val createFilePath = File(recordPath.absolutePath +"/" +
                Calendar.getInstance().get(Calendar.YEAR) + Calendar.getInstance().get(Calendar.MONTH) + Calendar.getInstance().get(Calendar.DATE) +
                "/" +Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"/"+ Calendar.getInstance().get(Calendar.MINUTE)/10
        )
    }
}