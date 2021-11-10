package jsy.sample.testwalkietalkie.view.activity

import android.media.MediaPlayer
import android.util.Log
import androidx.activity.viewModels
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ActivityMediaBinding
import jsy.sample.testwalkietalkie.view.base.BaseActivity
import jsy.sample.testwalkietalkie.view.model.MediaViewModel
import java.util.*
import jsy.sample.testwalkietalkie.view.recyclerView.MediaTimeLineRecyclerView
import java.lang.Exception

class MediaActivity : BaseActivity<ActivityMediaBinding>(R.layout.activity_media) {

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41


    private val mediaPlayer = MediaPlayer()
    private val _mediaViewModel : MediaViewModel by viewModels()

    override fun ActivityMediaBinding.init() {
//        val mediaPlayer = MediaPlayer.create(this)
        mediaViewModel = _mediaViewModel

        _mediaViewModel.getFolderFileList()


//        rvListMedia.layoutManager = GridLayoutManager(this@MediaActivity, 2)


        initObserve()


    }

    private fun initObserve(){

        val adapter = MediaTimeLineRecyclerView.MediaTimeLineAdapter(_mediaViewModel)
        _mediaViewModel.listMediaTimeLine.observe(binding.lifecycleOwner!!, { listMediaTimeLine ->
            if(listMediaTimeLine.isEmpty()) return@observe
            adapter.replaceAll(_mediaViewModel.listMediaTimeLine.value)

            for(list in _mediaViewModel.listMediaTimeLine.value!!)
            {
                val date = list.date

                Log.d(TAG,"리사이클러뷰 넘길때의 date : ${date}" )
            }

            binding.rvTimeLine.adapter = adapter
        })


//        val adapter = MediaRecyclerView.MediaAdapter(_mediaViewModel)
//
//        _mediaViewModel.listFiles.observe(binding.lifecycleOwner!!, { listFiles->
//            if(listFiles == null) return@observe
//            adapter.replaceAll(_mediaViewModel.listFiles.value!!)
//            binding.rvListMedia.adapter = adapter
//        })
//
        _mediaViewModel.currentFile.observe(binding.lifecycleOwner!!, {file->
            if(file == null ) return@observe
//            if(mediaPlayer.isPlaying){
//            }

            mediaPlayer.reset()
            try {
                mediaPlayer.setDataSource(file.path)
                mediaPlayer.setDisplay(binding.svMedia.holder)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {

            }

        })


    }




}