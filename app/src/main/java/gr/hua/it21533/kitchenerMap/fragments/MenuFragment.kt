package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import gr.hua.it21533.kitchenerMap.R
import kotlinx.android.synthetic.main.menu_fragment.*

class MenuFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.menu_fragment, container,false)
    }

    override fun onStart() {
        super.onStart()
        nav_types_of_places.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_types_of_places")
        }
        nav_about.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_about")
        }
        nav_feedback.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_feedback")
        }
        nav_opacity_slider.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_opacity_slider")
        }
    }

}