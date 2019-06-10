package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.fragments.FeedbackFragment
import gr.hua.it21533.kitchenerMap.fragments.MenuFragment
import gr.hua.it21533.kitchenerMap.fragments.TypesOfPlacesFragment
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.networking.ApiModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_menu.*
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MapsActivity :
    AppCompatActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    OnMapReadyCallback,
    MenuView,
    MapsActivityView {

    private val TAG = "MAPS_ACTIVITY"
    private lateinit var baseMap: GoogleMap
    private lateinit var kitchenerMapOverlay: TileOverlay
    private lateinit var mapsPresenter: MapsActivityPresenter
    private var sliderVisible = true
    private var markersList = ArrayList<Marker>()
    private var hasInteractedWithSeekBar = false
    private val initialLatitude: Double = 35.175422
    private val initialLongitude: Double = 33.363597
    private val initialZoomLevel = 10.0f
    private val handler = Handler()
    private val typesOfPlacesFragment = TypesOfPlacesFragment()
    var queryMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initSlider()
        initSideMenu()
        loadLocale()
        queryMap["location"] = "$initialLatitude, $initialLongitude"
        mapsPresenter = MapsActivityPresenter(this, queryMap)
        mapsPresenter.loadMarkers()
        typesOfPlacesFragment.delegate = this
    }

    override fun onMapReady(googleMap: GoogleMap) {
        baseMap = googleMap
        initKitchenerMap()
        setBoundariesAndZoom()
        enableLocationFunctionality()
    }

    private fun initKitchenerMap() {
        var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                val reversedY = (1 shl zoom) - y - 1
                val s = String.format(
                    "https://gaia.hua.gr/tms/kitchener2/test/%d/%d/%d.png",
                    zoom, x, reversedY
                )
                try {
                    return URL(s)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
            }
        }
        kitchenerMapOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        kitchenerMapOverlay.transparency = 0f
    }

    private fun enableLocationFunctionality() {
        baseMap.setOnMyLocationButtonClickListener(this)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            baseMap.isMyLocationEnabled = true
        }
    }

    private fun setBoundariesAndZoom() {
        val boundaries = LatLngBounds(LatLng(34.476619, 32.163363), LatLng(35.847896, 34.838356))
        baseMap.setLatLngBoundsForCameraTarget(boundaries)
        baseMap.setMaxZoomPreference(15.0f)
        baseMap.setMinZoomPreference(7.0f)
        baseMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(initialLatitude, initialLongitude),
            initialZoomLevel))
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun displayMarkers(markers: Array<ApiModel.Results>?) {
        removeMarkers()
        markers?.forEach {
            val marker = baseMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.geometry.location.lat, it.geometry.location.lng))
                    .title(it.name ?: "No title given by the API")
                    .snippet(it.vicinity ?: "No description given by the API")
                    .alpha(0.8f)
            )
            markersList.add(marker)
        }
        hideLoading()
    }

    private fun removeMarkers() {
        markersList.forEach {
            it.remove()
        }
    }

    private fun initSlider() {
        mapSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                kitchenerMapOverlay.transparency = 1 - (progress.toFloat() / 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                hasInteractedWithSeekBar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

        })
    }

    private fun initSideMenu() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            MenuFragment()
        ).commit()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    fun replaceMenuFragments(menuId: String) {
        when (menuId) {
            "nav_types_of_places" -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    typesOfPlacesFragment
                ).commit()
            }
            "nav_feedback" -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    FeedbackFragment()
                ).commit()
            }
            "nav_about" -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            "nav_main_menu" -> {
                backToMenu()
            }
            "nav_opacity_slider" -> {
                toggleSlider()
            }
            else -> {
                Log.d(TAG, "Wrong string sent to function")
            }
        }
    }

    private fun toggleSlider() {
        drawer_layout.closeDrawers()
        sliderVisible = !sliderVisible
        if (hasInteractedWithSeekBar) hasInteractedWithSeekBar = false
        mapSlider.visibility = if (sliderVisible) GONE else VISIBLE
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (!hasInteractedWithSeekBar) {
                mapSlider.visibility = GONE
                sliderVisible = true
                nav_opacity_slider.isChecked = false
                hasInteractedWithSeekBar = false
            }
        }, 5000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun didFilterChange(filterValue: String, filterType: String) {
        queryMap[filterType] = filterValue
        mapsPresenter.loadMarkers()
        showLoading()
    }

    override fun backToMenu() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            MenuFragment()
        ).commit()
    }

    override fun showLoading() {
        loadingAnimation.visibility = VISIBLE
    }

    override fun hideLoading() {
        loadingAnimation.visibility = GONE
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My Lang", "")
        setLocale(language, false)
    }

    fun setLocale(lang: String, reload: Boolean) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My Lang", lang)
        editor.apply()
        if(reload) {
            this.recreate()
        }
    }

    override fun uploadPhoto(currentPhotoPath: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
