package gr.hua.it21533.kitchenerMap.multiCheckExpandableList

import android.view.View
import android.widget.Checkable
import android.widget.CheckedTextView
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder
import gr.hua.it21533.kitchenerMap.R

class MultyCheckMapLayerViewHolder(itemView: View) : CheckableChildViewHolder(itemView) {

    private val childCheckedTextView: CheckedTextView = itemView.findViewById(R.id.list_item_multicheck_artist_name) as CheckedTextView

    override fun getCheckable(): Checkable {
        return childCheckedTextView
    }

    fun setArtistName(name: String) {
        childCheckedTextView.text = name
    }
}