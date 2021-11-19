package jsy.sample.testwalkietalkie.view.recyclerView

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.data.MediaTimeLine
import jsy.sample.testwalkietalkie.databinding.ItemTimeLineBinding
import jsy.sample.testwalkietalkie.utils.dpToPx
import jsy.sample.testwalkietalkie.utils.pxToDp
import jsy.sample.testwalkietalkie.view.base.BaseRecyclerView
import jsy.sample.testwalkietalkie.view.model.MediaViewModel
import jsy.sample.testwalkietalkie.view.model.MediaViewModel.Companion.firstMediaViewHeightCheck
import jsy.sample.testwalkietalkie.view.model.MediaViewModel.Companion.tvTimeHeight
import jsy.sample.testwalkietalkie.view.model.MediaViewModel.Companion.viewHeight

class MediaTimeLineRecyclerView() : BaseRecyclerView() {

    class MediaTimeLineAdapter(private val mediaViewModel: MediaViewModel) : BaseRecyclerView.Adapter<MediaTimeLine, ItemTimeLineBinding>(
        R.layout.item_time_line,
        BR.mediaTimeLine){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaTimeLineViewHolder {
            return MediaTimeLineViewHolder(
                parent = parent,
                mediaViewModel = mediaViewModel
            )
        }

    }

    class MediaTimeLineViewHolder(parent: ViewGroup, private val mediaViewModel: MediaViewModel) : BaseRecyclerView.ViewHolder<ItemTimeLineBinding>(
        layoutResId = R.layout.item_time_line,
        parent = parent,
        bindingVariableId = BR.mediaTimeLine
    ) {

        override fun onBindViewHolder(item: Any?) {
            super.onBindViewHolder(item)

            if(firstMediaViewHeightCheck)
            {
                firstMediaViewHeightCheck = false

                itemView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        viewHeight = itemView.height

                        mediaViewModel.setOneMinutePixelList() // view height pixel 설정


                    }
                })

                binding.timeShape.tvTime.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        binding.timeShape.tvTime.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        tvTimeHeight = binding.timeShape.tvTime.height
                    }
                })
            }


            if((item as MediaTimeLine).timeCheck)
            {
                Log.d("아이템체크", "${item}")
                mediaViewModel.currentInvisibleIndex.observe(itemView.context as LifecycleOwner, { invisibleIndex->
                    if(adapterPosition == invisibleIndex)
                    {
                        binding.timeShape.tvTime.visibility = View.INVISIBLE
                    }
                })

                mediaViewModel.visibleIndex.observe(itemView.context as LifecycleOwner, { visibleIndex ->
                    if(adapterPosition == visibleIndex)
                    {
                        binding.timeShape.tvTime.visibility = View.VISIBLE
                    }
                })
            }




        }
    }

}