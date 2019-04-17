package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container,false)
    }

    override fun onStart() {
        super.onStart()
        var mapsActivity = activity as MapsActivity
        nav_types_of_places.setOnClickListener {
            mapsActivity.replaceMenuFragments("nav_types_of_places")
        }
        nav_about.setOnClickListener {
            mapsActivity.replaceMenuFragments("nav_about")
        }
        nav_feedback.setOnClickListener {
            mapsActivity.replaceMenuFragments("nav_feedback")
        }
        nav_opacity_slider.setOnClickListener {
            mapsActivity.replaceMenuFragments("nav_opacity_slider")
        }
    }
}

