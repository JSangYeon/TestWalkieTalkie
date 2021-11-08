package jsy.sample.testwalkietalkie.view.recyclerView

import android.util.Log
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.databinding.ItemMediaFileBinding
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import java.io.File

class MediaRecyclerView : BaseRecyclerView() {

    class MediaAdapter : BaseRecyclerView.Adapter<File,ItemMediaFileBinding>(R.layout.item_media_file,
        BR.file){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : MediaViewHolder(
                layoutResId = R.layout.item_media_file,
                parent = parent,
                bindingVariableId = BR.file
            ) {}
    }


    open class MediaViewHolder(layoutResId: Int, parent: ViewGroup, bindingVariableId: Int?) : BaseRecyclerView.ViewHolder<ItemMediaFileBinding>(
        layoutResId = layoutResId,
        parent = parent,
        bindingVariableId = bindingVariableId
    ) {
        override fun onBindViewHolder(item: Any?) {
            super.onBindViewHolder(item)
            val file = item as File
            itemView.setOnClickListener { Log.d("클릭 테스트", "file ${file.name}") }
        }
    }


}