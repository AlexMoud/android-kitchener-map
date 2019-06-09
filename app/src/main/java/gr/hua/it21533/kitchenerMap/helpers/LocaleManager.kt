package gr.hua.it21533.kitchenerMap.helpers

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import gr.hua.it21533.kitchenerMap.helpers.PreferenceHelper.get
import gr.hua.it21533.kitchenerMap.helpers.PreferenceHelper.set
import java.util.*

object LocaleManager {

    val SELECTED_LANGUAGE = "MEW_CURRENT_-- USER_LANGUAGE"
    var mSharedPreference: SharedPreferences? = null

    var englishFlag = "en"
    var greekFlag = "el"

    fun setLocale(context: Context?): Context {
        val cl = getCurrentLanguage(context)
        return updateResources(context!!, getCurrentLanguage(context)!!)
    }

    inline fun setNewLocale(context: Context, language: String) {
        persistLanguagePreference(context, language)
        updateResources(context, language)
    }

    inline fun getCurrentLanguage(context: Context?): String? {

        var mCurrentLanguage: String?

        if (mSharedPreference == null)
            mSharedPreference = PreferenceHelper.defaultPrefs(context!!)

        mCurrentLanguage = mSharedPreference!![SELECTED_LANGUAGE]

        return mCurrentLanguage
    }

    fun persistLanguagePreference(context: Context, language: String) {
        if (mSharedPreference == null)
            mSharedPreference = PreferenceHelper.defaultPrefs(context)

        mSharedPreference!![SELECTED_LANGUAGE] = language

    }

    fun updateResources(context: Context, language: String): Context {

        var contextFun = context

        var locale = Locale(language)
        Locale.setDefault(locale)

        var resources = context.resources
        var configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale)
            contextFun = context.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        }
        return contextFun
    }
}