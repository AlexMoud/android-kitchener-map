package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnCheckChildClickListener
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.helpers.LayersHelper
import gr.hua.it21533.kitchenerMap.helpers.TileProviderFactory
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.models.MapLayer
import gr.hua.it21533.kitchenerMap.multiCheckExpandableList.MultiCheckMapLayerParentAdapter
import kotlinx.android.synthetic.main.fragment_types_of_places.*

class TypesOfPlacesFragment: Fragment(), OnCheckChildClickListener {

    private lateinit var adapter: MultiCheckMapLayerParentAdapter
    private var started = false
    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_types_of_places, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (started) {
            return
        }
        started = true
        addTypesOfPlacesCheckboxes()
        backToMenu.setOnClickListener {
            delegate?.backToMenu()
        }
    }

    fun refresh() {
        if (adapter.groups.size == 0) {
            addTypesOfPlacesCheckboxes()
        }
    }

    private fun addTypesOfPlacesCheckboxes() {
        typesCheckboxes.layoutManager = LinearLayoutManager(context)

        LayersHelper.reloadLayers()
        adapter = MultiCheckMapLayerParentAdapter(LayersHelper.data)
        if (adapter.groups.size > 0) {
            adapter.checkChild(true, 0, 0)
        }
        typesCheckboxes.adapter = adapter
        adapter.setChildClickListener(this )
    }

    override fun onCheckChildCLick(v: View?, checked: Boolean, group: CheckedExpandableGroup?, childIndex: Int) {
        val data = (group?.items?.get(childIndex) as MapLayer).data

        if (data.type == "tile" && data.userOrder <= 2) {
            delegate?.didSelectMapOverlay(data, childIndex)
            return
        } else if (data.type == "tile") {
            delegate?.didSelectMapOverlay(data, data.userOrder)
            return
        }
        if (checked) {
            TileProviderFactory.layers.add(data)
        }else {
            TileProviderFactory.layers.remove(data)
        }
        delegate?.didFilterChange()
    }

    fun clear() {
        (typesCheckboxes.adapter as MultiCheckMapLayerParentAdapter?)?.clearChoices()
        if (adapter.groups.size > 0) {
            adapter.setChildClickListener(null)
            adapter.checkChild(true, 0, 0)
            adapter.setChildClickListener(this)
        }
    }
}