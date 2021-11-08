package jsy.sample.testwalkietalkie.view.activity

import androidx.activity.viewModels
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ActivityMediaBinding
import jsy.sample.testwalkietalkie.databinding.ItemMediaFileBinding
import jsy.sample.testwalkietalkie.view.base.BaseActivity
import jsy.sample.testwalkietalkie.view.model.MediaViewModel
import java.util.*
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import jsy.sample.testwalkietalkie.view.recyclerView.MediaRecyclerView
import java.io.File

class MediaActivity : BaseActivity<ActivityMediaBinding>(R.layout.activity_media) {

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41

    private val _mediaViewModel : MediaViewModel by viewModels()

    override fun ActivityMediaBinding.init() {
//        val mediaPlayer = MediaPlayer.create(this)
        mediaViewModel = _mediaViewModel

        _mediaViewModel.getFolderFileList()

        initObserve()

    }

    private fun initObserve(){

//        val adapter = object : BaseRecyclerView.Adapter<File, ItemMediaFileBinding>(
//            layoutResId = R.layout.item_media_file,
//            bindingVariableId = BR.file
//        ) {}

        val adapter = MediaRecyclerView.MediaAdapter()
        _mediaViewModel.listFiles.observe(binding.lifecycleOwner!!, { listFiles->
            when(listFiles) {
                null -> return@observe
                else -> {
//                    Log.d(TAG, "listFiles : ${Arrays.toString(listFiles)}")
//                    recyclerview
                    adapter.replaceAllArray(_mediaViewModel.listFiles.value!!)
                    binding.rvListMedia.adapter = adapter
                }
            }
        })


    }




}