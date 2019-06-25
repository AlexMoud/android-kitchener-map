package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private val TAG = "SEARCH_FRAGMENT"
    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onStart() {
        super.onStart()
        backToMenu.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_main_menu")
        }
        searchForPlace.setOnClickListener {
            val searchValue = searchEditText.text.toString()
            delegate?.onTextSearch(searchValue, "query")
        }
    }
}

