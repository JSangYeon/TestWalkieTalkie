package jsy.sample.testwalkietalkie.view.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jsy.sample.testwalkietalkie.utils.PathUtils
import java.io.File

class MediaViewModel : ViewModel() {

    private val TAG = "MediaViewModel"

    private val _listFiles = MutableLiveData<Array<File>?>()
    val listFiles : LiveData<Array<File>?>
        get() = _listFiles


    init {
        _listFiles.value = null
    }

    fun getFolderFileList() {
        val dir = PathUtils.recordPath
        if (!dir.isDirectory) {
            if (!dir.mkdirs()) {
                Log.d(TAG, " aaaaa")
            }
        }

        when(dir.listFiles())
        {
            null -> return
            else -> {
                _listFiles.value = dir.listFiles()!!
                for(file in dir.listFiles()!!)
                {
                    Log.d(TAG, " aaaaaf : " + file.path.toString() + " , " + file.path.endsWith(".mp4"))

                }

            }
        }
    }

}