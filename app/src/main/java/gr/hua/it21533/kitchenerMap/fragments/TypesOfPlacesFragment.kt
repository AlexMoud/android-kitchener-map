package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MenuView
import gr.hua.it21533.kitchenerMap.adapters.TypesAdapter
import gr.hua.it21533.kitchenerMap.models.TypesModel
import kotlinx.android.synthetic.main.types_of_places_fragment.*

class TypesOfPlacesFragment: Fragment() {

    private var types = ArrayList<TypesModel>()
    private var selectedTypes = ArrayList<String>()
    private var started = false
    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.types_of_places_fragment, container, false)
        savedInstanceState?.let {
            Log.d("CHECKBOXES","inside savedInstance")
            selectedTypes = savedInstanceState.getStringArrayList("selectedCheckboxes")
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if(!started) {
            addTypesOfPlacesCheckboxes()
            backToMenu.setOnClickListener {
                delegate?.backToMenu()
            }
        }
        started = true
    }

    private fun addTypesOfPlacesCheckboxes() {
        types.add(TypesModel("cafe", "Coffee Shops"))
        types.add(TypesModel("bank", "Banks"))
        types.add(TypesModel("lodging", "Lodging"))
        types.add(TypesModel("museum", "Museums"))
        types.add(TypesModel("locality", "Locality"))
        types.add(TypesModel("political", "Political"))
        typesCheckboxes.layoutManager = LinearLayoutManager(context)
        typesCheckboxes.adapter = TypesAdapter(types, context!!, selectedTypes) { item: String -> itemTypeClicked(item) }
        Log.d("CHECKBOXES","${selectedTypes.size}")
    }

    private fun itemTypeClicked(item: String) {
        if (selectedTypes.contains(item)) {
            selectedTypes.remove(item)
        } else {
            selectedTypes.add(item)
        }
        delegate?.didFilterChange(selectedTypes.joinToString(), "types")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("CHECKBOXES","inside onSaveInstanceState")
        outState.putStringArrayList("selectedCheckboxes", selectedTypes)
    }
}