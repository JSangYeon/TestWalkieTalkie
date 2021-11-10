package jsy.sample.testwalkietalkie.view.recyclerView

import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.data.MediaFile
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.databinding.ItemMediaFileBinding
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import jsy.sample.testwalkietalkie.view.model.MediaViewModel

class MediaTimeLineRecyclerView() : BaseRecyclerView() {
    class MediaTimeLineAdapter(private val mediaViewModel: MediaViewModel) : BaseRecyclerView.Adapter<MediaTimeLine, ItemMediaFileBinding>(
        R.layout.item_time_line,
        BR.mediaTimeLine){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : MediaTimeLineViewHolder(
                parent = parent,
                mediaViewModel = mediaViewModel
            ) {}

//        override fun onBindViewHolder(holder: ViewHolder<ItemMediaFileBinding>, position: Int) {
//            onBindViewHolder(holder, position)
//
//        }
    }

    open class MediaTimeLineViewHolder(parent: ViewGroup, private val mediaViewModel: MediaViewModel) : BaseRecyclerView.ViewHolder<ItemMediaFileBinding>(
        layoutResId = R.layout.item_time_line,
        parent = parent,
        bindingVariableId = BR.mediaTimeLine
    ) {
//        override fun onBindViewHolder(item: Any?) {
//            super.onBindViewHolder(item)
//
////            val mediaFile = item as MediaFile
////            binding.ivFileThumbnail.setImageBitmap(mediaFile.thumbnail)
////            itemView.setOnClickListener { mediaViewModel.setCurrentFile(mediaFile.file) }
//        }
    }
}