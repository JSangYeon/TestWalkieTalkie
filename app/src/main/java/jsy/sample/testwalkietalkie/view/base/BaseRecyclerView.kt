package jsy.sample.testwalkietalkie.view.base

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerView {

    abstract class Adapter<ITEM : Any, B : ViewDataBinding>(
        @LayoutRes private val layoutResId: Int,
        private val bindingVariableId: Int? = null
    ) : RecyclerView.Adapter<ViewHolder<B>>() {

        protected val items = mutableListOf<ITEM>()

        fun replaceAll(items: List<ITEM>?) {
            items?.let {
                this.items.run {
                    clear()
                    addAll(it)
                }
            }
        }

        fun replaceAllArray(items: Array<ITEM>?) {
            items?.let {
                this.items.run {
                    clear()
                    addAll(it)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : ViewHolder<B>(
                layoutResId = layoutResId,
                parent = parent,
                bindingVariableId = bindingVariableId
            ) {}

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
            holder.onBindViewHolder(items[position])
            holder.setIsRecyclable(false)
        }
    }

    abstract class ViewHolder<B : ViewDataBinding>(
        @LayoutRes layoutResId: Int,
        parent: ViewGroup,
        val bindingVariableId: Int?
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
    ) {

        protected val binding: B = DataBindingUtil.bind(itemView)!!

        open fun onBindViewHolder(item: Any?) {
            try {
                bindingVariableId?.let {
                    binding.setVariable(it, item)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}