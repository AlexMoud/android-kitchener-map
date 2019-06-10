package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment: Fragment() {

    private val TAG = "MENU_FRAGMENT"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(gr.hua.it21533.kitchenerMap.R.layout.fragment_menu, container,false)
    }

    override fun onStart() {
        super.onStart()
        val mapsActivity = activity as MapsActivity
        val progressBar = mapsActivity.findViewById(gr.hua.it21533.kitchenerMap.R.id.mapSlider) as ProgressBar
        if(progressBar.visibility == View.VISIBLE) nav_opacity_slider.isChecked = true
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
        greek_language.setOnClickListener {
            mapsActivity.setLocale("el", true)
        }
        english_language.setOnClickListener {
            mapsActivity.setLocale("en", true)
        }
    }

}

