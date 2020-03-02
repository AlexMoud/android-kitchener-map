package gr.hua.it21533.kitchenerMap.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.ContextThemeWrapper
import com.google.gson.JsonParser
import gr.hua.it21533.kitchenerMap.models.Gravoura
import gr.hua.it21533.kitchenerMap.models.SearchResult
import gr.hua.it21533.kitchenerMap.networking.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        var dLocale: Locale? = null
    }

    init {
        updateConfig(this)
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        if(dLocale==Locale("") ) // Do nothing if dLocale is null
            return

        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }
}