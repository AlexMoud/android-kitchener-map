package gr.hua.it21533.kitchenerMap.multiCheckExpandableList

import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import gr.hua.it21533.kitchenerMap.R

class MapLayerParentViewHolder(itemView: View) : GroupViewHolder(itemView) {

    private val genreName: TextView = itemView.findViewById(R.id.list_item_genre_name) as TextView
    private val arrow: ImageView = itemView.findViewById(R.id.list_item_genre_arrow) as ImageView

    fun setTitle(genre: ExpandableGroup<*>) {
        genreName.text = genre.title
    }

    override fun expand() {
        animateExpand()
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand() {
        val rotate = RotateAnimation(360f, 180f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 200
        rotate.fillAfter = true
        arrow.animation = rotate
    }

    private fun animateCollapse() {
        val rotate = RotateAnimation(180f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 200
        rotate.fillAfter = true
        arrow.animation = rotate
    }
}