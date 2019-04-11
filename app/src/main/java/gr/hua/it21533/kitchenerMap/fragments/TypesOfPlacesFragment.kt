package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.adapters.TypesAdapter
import gr.hua.it21533.kitchenerMap.models.TypesModel
import kotlinx.android.synthetic.main.types_of_places_fragment.*

class TypesOfPlacesFragment: Fragment() {

    private var types: ArrayList<TypesModel> = ArrayList()
    private var selectedTypes = ArrayList<String>()
    var delegate: FilteringListener? = null

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
        typesCheckboxes.adapter =
            TypesAdapter(
                types,
                context!!
            ) { item: String -> itemTypeClicked(item) }
    }

    private fun itemTypeClicked(item: String) {
        if (selectedTypes.contains(item)) {
            //comment
            delegate?.didDeselect(item)
            selectedTypes.remove(item)
        } else {
            //comment
            delegate?.didSelectFilter(item)
            selectedTypes.add(item)
        }
        //αυτο δημιουργει συνδεση του μενού με το activity. μπορεί στην προκειμένη να μην μας ενοχλεί αλλά
        (activity as MapsActivity).queryMap.put("types", selectedTypes.joinToString())
        (activity as MapsActivity).searchForPlaces()
        // αν το μενού το ήθελες σε κάθε οθόνη θα ήθελες έναν γενικό τρόπο για να το χρησιμοποιείς.
        //Δημιουργόντας έναν listener (delegate, πες το όπως θες) "εγραφεται" όποιος θέλει να χρησιμοποιήσει το μενού
        //προσπάθησε να χρησιμοποιείς interfaces ή closures.
        // Π.χ. το "back to menu" πάλι θα έπρεπε να είναι function του interface και να έκανε ότι ήθελε το activity σου όταν πατάει το back ο χρήστης
        //Το κάνουμε συνήθως optional το interface, μπορείς να έχεις και optional functions
    }

    //ένα closure εδώ θα μπορούσε να είναι το εξής
    var onFilterSelected: ((TypesModel)->Unit)? = null
    //και καλείται ως εξής
    fun onItemTypeSelected(item: TypesModel) {
        onFilterSelected?.invoke(item)
    }
}

//comment
interface FilteringListener {
    fun didSelectFilter(filter: String)
    fun didDeselect(filter: String)
}