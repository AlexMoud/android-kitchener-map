package gr.hua.it21533.kitchenerMap.multiCheckExpandableList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter
import com.thoughtbot.expandablecheckrecyclerview.ChildCheckController
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.models.MapLayer
import gr.hua.it21533.kitchenerMap.models.MapLayerParent

class MultiCheckMapLayerParentAdapter(groups: MutableList<MapLayerParent>) : CheckableChildRecyclerViewAdapter<MapLayerParentViewHolder, MultyCheckMapLayerViewHolder>(groups) {

    override fun onCreateCheckChildViewHolder(parent: ViewGroup, viewType: Int): MultyCheckMapLayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_map_layer, parent, false)
        return MultyCheckMapLayerViewHolder(view)
    }

    override fun onBindCheckChildViewHolder(holder: MultyCheckMapLayerViewHolder, position: Int, group: CheckedExpandableGroup, childIndex: Int
    ) {
        val mapLayer = group.items[childIndex] as MapLayer
        holder.setArtistName(mapLayer.name!!)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): MapLayerParentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_parent, parent, false)
        return MapLayerParentViewHolder(view)
    }

    override fun onBindGroupViewHolder(holder: MapLayerParentViewHolder, flatPosition: Int, group: ExpandableGroup<*>) {
        holder.setTitle(group)
    }

    override fun onChildCheckChanged(view: View?, checked: Boolean, flatPos: Int) {
        super.onChildCheckChanged(view, checked, flatPos)
    }


}
