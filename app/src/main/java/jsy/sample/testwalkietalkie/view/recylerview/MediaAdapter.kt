package jsy.sample.testwalkietalkie.view.recylerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jsy.sample.testwalkietalkie.R

class MediaAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<MediaViewHolder>() {



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MediaViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_media_file, viewGroup, false)

        return MediaViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: MediaViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        MediaViewHolder..text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}