package gr.hua.it21533.kitchenerMap

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.types_of_places_fragment.*

class TypesOfPlacesFragment: Fragment() {

    private var types: ArrayList<TypesModel> = ArrayList()
    private var selectedTypes = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.types_of_places_fragment, container,false)
    }

    override fun onStart() {
        super.onStart()
        addTypesOfPlacesCheckboxes()
        backToMenu.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_main_menu")
        }
    }

    private fun addTypesOfPlacesCheckboxes() {
        types.add(TypesModel("cafe", "Coffee Shops"))
        types.add(TypesModel("bank", "Banks"))
        types.add(TypesModel("lodging", "Lodging"))
        types.add(TypesModel("museum", "Museums"))
        types.add(TypesModel("locality", "Locality"))
        types.add(TypesModel("political", "Political"))
        typesCheckboxes.layoutManager = LinearLayoutManager(context)
        typesCheckboxes.adapter = TypesAdapter(types, context!!) { item: String -> itemTypeClicked(item) }
    }

    private fun itemTypeClicked(item: String) {
        if (selectedTypes.contains(item)) {
            selectedTypes.remove(item)
        } else {
            selectedTypes.add(item)
        }
        (activity as MapsActivity).queryMap.put("types", selectedTypes.joinToString())
        (activity as MapsActivity).searchForPlaces()
    }

}