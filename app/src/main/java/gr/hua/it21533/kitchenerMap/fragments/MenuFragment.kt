package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment : Fragment() {

    private val TAG = "MENU_FRAGMENT"
    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onStart() {
        super.onStart()
        nav_types_of_places.setOnClickListener {
            delegate?.replaceMenuFragments("nav_types_of_places")
        }
        nav_about.setOnClickListener {
            delegate?.replaceMenuFragments("nav_about")
        }
        nav_feedback.setOnClickListener {
            delegate?.replaceMenuFragments("nav_feedback")
        }
        nav_opacity_slider.setOnClickListener {
            delegate?.replaceMenuFragments("nav_opacity_slider")
        }
        greek_language.setOnClickListener {
            delegate?.setLocale("el", true)
        }
        english_language.setOnClickListener {
            delegate?.setLocale("en", true)
        }
    }
}

