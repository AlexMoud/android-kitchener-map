package gr.hua.it21533.kitchenerMap

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.gson.GsonBuilder
import gr.hua.it21533.kitchenerMap.activities.BaseActivity
import gr.hua.it21533.kitchenerMap.models.Gravoura
import java.util.*


class KitchenerMap : Application() {

    var selectedLocale: String = ""

    init {
        instance = this
    }

    companion object {
        private var instance: KitchenerMap? = null

        fun isNetworkAvailable(): Boolean {
            val connectivityManager = instance?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun applicationContext() : KitchenerMap {
            return instance as KitchenerMap
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadSavedLocale()
    }


    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
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

    fun saveGravoura(data: Gravoura, key: String) {
        val editor = getSharedPreferences("Gravoura", Context.MODE_PRIVATE).edit()
        val inString = GsonBuilder().create().toJson(data)
        editor.putString(key, inString).apply()

    }

    fun getGraoures(key: String): Gravoura? {
        //We read JSON String which was saved.
        val sharedPreferences = getSharedPreferences("Gravoura", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString(key, null)
        return if (value != null) {
            GsonBuilder().create().fromJson(value, Gravoura::class.java)
        } else {
            null
        }
    }
}