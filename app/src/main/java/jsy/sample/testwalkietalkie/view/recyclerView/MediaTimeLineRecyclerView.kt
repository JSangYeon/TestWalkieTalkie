package jsy.sample.testwalkietalkie.view.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.library.baseAdapters.BR
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.application.MyApplication
import jsy.sample.testwalkietalkie.data.MediaFile
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.databinding.ItemMediaFileBinding
import jsy.sample.testwalkietalkie.databinding.ItemTimeLineBinding
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import jsy.sample.testwalkietalkie.view.model.MediaViewModel

class MediaTimeLineRecyclerView() : BaseRecyclerView() {
    class MediaTimeLineAdapter(private val mediaViewModel: MediaViewModel) : BaseRecyclerView.Adapter<MediaTimeLine, ItemTimeLineBinding>(
        R.layout.item_time_line,
        BR.mediaTimeLine){


//        override fun onCreateViewHolder(
//            parent: ViewGroup,
//            viewType: Int
//        ): ViewHolder<ItemTimeLineBinding> {
//
//
//
//            return super.onCreateViewHolder(parent, viewType)
//        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : MediaTimeLineViewHolder(
                parent = parent,
                mediaViewModel = mediaViewModel
            ) {

            }

//        override fun onBindViewHolder(holder: ViewHolder<ItemMediaFileBinding>, position: Int) {
//            onBindViewHolder(holder, position)
//
//        }
    }

    open class MediaTimeLineViewHolder(parent: ViewGroup, private val mediaViewModel: MediaViewModel) : BaseRecyclerView.ViewHolder<ItemTimeLineBinding>(
        layoutResId = R.layout.item_time_line,
        parent = parent,
        bindingVariableId = BR.mediaTimeLine
    ) {
        override fun onBindViewHolder(item: Any?) {
            super.onBindViewHolder(item)
            Log.d("currentDate", "date : ${binding.mediaTimeLine!!.date}")

            if(binding.mediaTimeLine!!.mediaFileList!=null) {
                val adapter = MediaRecyclerView.MediaAdapter(mediaViewModel)
                adapter.replaceAll(binding.mediaTimeLine!!.mediaFileList)
                binding.rvMedia.adapter = adapter
            }

        }


    }
}