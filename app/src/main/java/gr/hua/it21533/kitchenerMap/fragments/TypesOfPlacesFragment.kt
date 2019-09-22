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

    private var started = false
    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_types_of_places, container, false)
    }

    override fun onStart() {
        super.onStart()

        started = true
        addTypesOfPlacesCheckboxes()
        backToMenu.setOnClickListener {
            delegate?.backToMenu()
        }
    }

    private fun addTypesOfPlacesCheckboxes() {
        typesCheckboxes.layoutManager = LinearLayoutManager(context)

        val adapter = MultiCheckMapLayerParentAdapter(LayersHelper.data)
        typesCheckboxes.adapter = adapter
        adapter.setChildClickListener(this )
    }

    override fun onCheckChildCLick(v: View?, checked: Boolean, group: CheckedExpandableGroup?, childIndex: Int) {
        val layer = (group?.items?.get(childIndex) as MapLayer).data.src
        if (checked) {
            TileProviderFactory.layers.add(layer)
        }else {
            TileProviderFactory.layers.remove(layer)
        }
        delegate?.didFilterChange()
    }
}