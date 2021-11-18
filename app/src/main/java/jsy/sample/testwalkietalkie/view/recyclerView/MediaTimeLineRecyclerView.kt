package jsy.sample.testwalkietalkie.view.recyclerView

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.databinding.ItemTimeLineBinding
import jsy.sample.testwalkietalkie.utils.dpToPx
import jsy.sample.testwalkietalkie.utils.pxToDp
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import jsy.sample.testwalkietalkie.view.model.MediaViewModel
import jsy.sample.testwalkietalkie.view.model.MediaViewModel.Companion.firstMediaViewHeightCheck
import jsy.sample.testwalkietalkie.view.model.MediaViewModel.Companion.viewHeight
import java.util.*
import kotlin.collections.ArrayList

class MediaTimeLineRecyclerView() : BaseRecyclerView() {

    class MediaTimeLineAdapter(private val mediaViewModel: MediaViewModel) : BaseRecyclerView.Adapter<MediaTimeLine, ItemTimeLineBinding>(
        R.layout.item_time_line,
        BR.mediaTimeLine){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaTimeLineViewHolder {
            return MediaTimeLineViewHolder(
                parent = parent,
                mediaViewModel = mediaViewModel)
        }

    }

    class MediaTimeLineViewHolder(parent: ViewGroup, private val mediaViewModel: MediaViewModel) : BaseRecyclerView.ViewHolder<ItemTimeLineBinding>(
        layoutResId = R.layout.item_time_line,
        parent = parent,
        bindingVariableId = BR.mediaTimeLine
    ) {

        override fun onBindViewHolder(item: Any?) {
            super.onBindViewHolder(item)
            Log.d("미디어뷰 번호" , "${adapterPosition}")

            if(firstMediaViewHeightCheck)
            {
                firstMediaViewHeightCheck = false

                itemView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        viewHeight = itemView.height
                        Log.d("미디어뷰 높이" , "height : ${itemView.height}, measureHeight : ${itemView.measuredHeight}\n" +
                                "heightDp : ${itemView.height.pxToDp}, measureHeightDp : ${itemView.measuredHeight.pxToDp}\n" +
                                "dpTopx : ${itemView.height.pxToDp.toInt().dpToPx}")

                        mediaViewModel.setOneMinutePixelList() // view height pixel 설정
                    }
                })
            }


        }
    }

}