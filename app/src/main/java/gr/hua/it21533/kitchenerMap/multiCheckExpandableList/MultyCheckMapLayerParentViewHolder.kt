package gr.hua.it21533.kitchenerMap.multiCheckExpandableList

import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup
import gr.hua.it21533.kitchenerMap.models.MapLayer

class MultyCheckMapLayerParentViewHolder(title: String, items: List<MapLayer>, val iconResId: Int) :
    MultiCheckExpandableGroup(title, items)