package gr.hua.it21533.kitchenerMap

import android.app.Application
import android.content.Context
import gr.hua.it21533.kitchenerMap.activities.BaseActivity
import java.util.*

class KitchenerMap : Application() {

    var selectedLocale: String = ""
    var isOffline = false

    init {
        instance = this
    }

    companion object {
        private var instance: KitchenerMap? = null

        fun applicationContext() : KitchenerMap {
            return instance as KitchenerMap
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadSavedLocale()
    }

    fun saveLocale(lang: String) {
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My Lang", lang)
        editor.apply()
        selectedLocale = lang
        BaseActivity.dLocale = Locale(lang) //set any locale you want here

    }

    private fun loadSavedLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        selectedLocale = sharedPreferences.getString("My Lang", "")
        BaseActivity.dLocale = Locale(selectedLocale)
    }
}