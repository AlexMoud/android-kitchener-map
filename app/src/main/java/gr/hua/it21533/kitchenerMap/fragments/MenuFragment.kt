package gr.hua.it21533.kitchenerMap.fragments

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_menu.*
import gr.hua.it21533.kitchenerMap.helpers.LocaleManager
import java.util.*
import android.content.Intent
import android.os.LocaleList


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
            setLocale()
        }
        english_language.setOnClickListener {
            setLocale()
        }
    }

    private fun setLocale() {
        val mapsActivity = activity as MapsActivity
        var mCurrentLanguage = LocaleManager.getCurrentLanguage(mapsActivity.applicationContext)
        Log.d(TAG,"$mCurrentLanguage")
        if (mCurrentLanguage == LocaleManager.englishFlag) {
            LocaleManager.setNewLocale(mapsActivity.applicationContext!!, LocaleManager.englishFlag)
        } else if (mCurrentLanguage == LocaleManager.greekFlag) {
            LocaleManager.setNewLocale(mapsActivity.applicationContext, LocaleManager.greekFlag)
        }
        activity?.recreate()
    }
}

