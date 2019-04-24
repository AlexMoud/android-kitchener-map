package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.adapters.TypesAdapter
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.models.TypesModel
import kotlinx.android.synthetic.main.fragment_types_of_places.*

class TypesOfPlacesFragment: Fragment() {

    private var types = ArrayList<TypesModel>()
    private var selectedTypes = ArrayList<String>()
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
        types.add(TypesModel("cafe", "Coffee Shops"))
        types.add(TypesModel("bank", "Banks"))
        types.add(TypesModel("lodging", "Lodging"))
        types.add(TypesModel("museum", "Museums"))
        types.add(TypesModel("locality", "Locality"))
        types.add(TypesModel("political", "Political"))
    }

    private fun addTypesOfPlacesCheckboxes() {
        typesCheckboxes.layoutManager = LinearLayoutManager(context)
        typesCheckboxes.adapter = TypesAdapter(types, context!!, selectedTypes) { item: String -> itemTypeClicked(item) }
    }

    private fun itemTypeClicked(item: String) {
        if (selectedTypes.contains(item)) {
            selectedTypes.remove(item)
        } else {
            selectedTypes.add(item)
        }
        delegate?.didFilterChange(selectedTypes.joinToString(), "types")
    }
}