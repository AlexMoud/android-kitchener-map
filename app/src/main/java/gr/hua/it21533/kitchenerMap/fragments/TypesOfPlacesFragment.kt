package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.adapters.MapLayersAdapter
import gr.hua.it21533.kitchenerMap.helpers.LayersHelper
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.models.Base
import gr.hua.it21533.kitchenerMap.multiCheckExpandableList.MultiCheckMapLayerParentAdapter
import kotlinx.android.synthetic.main.fragment_types_of_places.*

class TypesOfPlacesFragment: Fragment() {

    private var types = ArrayList<Base>()
    private var selectedTypes = ArrayList<Base>()
    private var started = false
    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_types_of_places, container, false)
    }

    override fun onStart() {
        super.onStart()
        if(!started) {
            addCheckboxesContent()
        }
        started = true
        addTypesOfPlacesCheckboxes()
        backToMenu.setOnClickListener {
            delegate?.backToMenu()
        }
    }

    private fun addCheckboxesContent() {
        types = LayersHelper.getLayersData()
    }

    private fun addTypesOfPlacesCheckboxes() {
        typesCheckboxes.layoutManager = LinearLayoutManager(context)
//        typesCheckboxes.adapter = MapLayersAdapter(types, context!!, selectedTypes) { item: String -> itemTypeClicked(item) }

        typesCheckboxes.adapter = MultiCheckMapLayerParentAdapter(LayersHelper.getLayerParents())
    }

    private fun itemTypeClicked(item: String) {
//        if (selectedTypes.contains(item)) {
//            selectedTypes.remove(item)
//        } else {
//            selectedTypes.add(item)
//        }
        delegate?.didFilterChange(selectedTypes.joinToString(), "types")
    }
}