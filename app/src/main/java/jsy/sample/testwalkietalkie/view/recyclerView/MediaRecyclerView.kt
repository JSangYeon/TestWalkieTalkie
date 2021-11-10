package jsy.sample.testwalkietalkie.view.recyclerView

import android.util.Log
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.data.MediaFile
import jsy.sample.testwalkietalkie.databinding.ItemMediaFileBinding
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import jsy.sample.testwalkietalkie.view.model.MediaViewModel
import java.io.File

class MediaRecyclerView() : BaseRecyclerView() {

    class MediaAdapter(private val mediaViewModel: MediaViewModel) : BaseRecyclerView.Adapter<MediaFile,ItemMediaFileBinding>(R.layout.item_media_file,
        BR.mediaFile){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : MediaViewHolder(
                parent = parent,
                mediaViewModel = mediaViewModel
            ) {}

//        override fun onBindViewHolder(holder: ViewHolder<ItemMediaFileBinding>, position: Int) {
//            super.onBindViewHolder(holder, position )
//        }
    }

    open class MediaViewHolder(parent: ViewGroup, private val mediaViewModel: MediaViewModel) : BaseRecyclerView.ViewHolder<ItemMediaFileBinding>(
        layoutResId = R.layout.item_media_file,
        parent = parent,
        bindingVariableId = BR.mediaFile
    ) {
        override fun onBindViewHolder(item: Any?) {
            super.onBindViewHolder(item)
            val mediaFile = item as MediaFile

            binding.ivFileThumbnail.setImageBitmap(mediaFile.thumbnail)

            itemView.setOnClickListener { mediaViewModel.setCurrentFile(mediaFile.file) }

        }
    }
}