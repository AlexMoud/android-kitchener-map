package gr.hua.it21533.kitchenerMap.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_policy.*

class PolicyFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_policy, container,false)
    }

    override fun onStart() {
        super.onStart()
        backToMenu.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_main_menu")
        }
        var url = "https://gaia.hua.gr/kitchener_review/privacy_$.html"
        url = if (KitchenerMap.applicationContext().selectedLocale == "el") {
            url.replace("$", "el")
        } else {
            url.replace("$", "en")
        }
        webview.loadUrl(url)
    }

}