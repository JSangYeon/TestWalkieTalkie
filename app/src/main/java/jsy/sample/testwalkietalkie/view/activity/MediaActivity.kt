package jsy.sample.testwalkietalkie.view.activity

import android.media.MediaPlayer
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ActivityMediaBinding
import jsy.sample.testwalkietalkie.utils.dpToPx
import jsy.sample.testwalkietalkie.utils.pxToDp
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





//        binding.rvTimeLine.addOnScrollListener(RecyclerView.OnScrollListener{})



        initObserve()


    }

    private fun initObserve(){

        val adapter = MediaTimeLineRecyclerView.MediaTimeLineAdapter(_mediaViewModel)

        with(binding)
        {
            rvTimeLine.adapter = adapter
            _mediaViewModel.listMediaTimeLine.observe(lifecycleOwner!!, { listMediaTimeLine ->
                if(listMediaTimeLine.isEmpty()) return@observe
                adapter.replaceAll(_mediaViewModel.listMediaTimeLine.value)

                for(list in _mediaViewModel.listMediaTimeLine.value!!)
                {
                    val date = list.date

                    Log.d(TAG,"리사이클러뷰 넘길때의 date : ${date}")
                }

            })

            _mediaViewModel.currentFile.observe(lifecycleOwner!!, {file->
                if(file == null ) return@observe

                mediaPlayer.reset()
                try {
                    mediaPlayer.setDataSource(file.path)
                    mediaPlayer.setDisplay(svMedia.holder)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: Exception) {

                }

            })


            _mediaViewModel.listOneMinutePixel.observe(lifecycleOwner!!, {
                Log.d("listOneMinutePixel", "changeCheckObserve")
                var lastScrollPosition = 0;

                with(rvTimeLine){
                    clearOnScrollListeners()
                    var currentHeight = 0;
                    addOnScrollListener(object : RecyclerView.OnScrollListener(){ //1칸의 height 을 구해서 포지션만큼 빼준값을 이용해 시간을 구해야함
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            lastScrollPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()


                            currentHeight += dy

                            Log.d("scrollChangeCheck", "lastScrollPosition : ${lastScrollPosition}\n" +
                                    "dy : ${dy.pxToDp} , currentHeight : ${currentHeight.pxToDp}\n" +
                                    "")

                            _mediaViewModel.getScrollTime(lastScrollPosition, currentHeight)

                        }
                    })
                }

            })

            _mediaViewModel.currentScrolledTime.observe(lifecycleOwner!!, { currentScrolledTime ->
                tvScrolledTime.text = currentScrolledTime
            })

        }



    }




}