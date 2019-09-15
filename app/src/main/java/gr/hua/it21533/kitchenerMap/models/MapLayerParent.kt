package gr.hua.it21533.kitchenerMap.models

import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup

class MapLayerParent(title: String, items: List<MapLayer>) : MultiCheckExpandableGroup(title, items) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is MapLayerParent) return false

        val genre = o as MapLayerParent?

        return title == genre!!.title

    }

    override fun hashCode(): Int {
        return title.length + items.size
    }
}
