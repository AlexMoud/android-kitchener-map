package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
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
    private var longClickMarkers = ArrayList<Marker>()
    private var hasInteractedWithSeekBar = false
    private val initialLatitude: Double = 35.17
    private val initialLongitude: Double = 33.36
    private val initialZoomLevel = 10.0f
    private val handler = Handler()
    private val typesOfPlacesFragment = TypesOfPlacesFragment()
    private val menuFragment = MenuFragment()
    private val feedbackFragment = FeedbackFragment()
    private var currentFragment: Fragment = menuFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initSlider()
        initSideMenu()
        loadLocale()
        initAllFragments()
        mapsPresenter = MapsActivityPresenter(this)
        mapsPresenter.addToQuery("location", "$initialLatitude, $initialLongitude")
        typesOfPlacesFragment.delegate = this
        menuFragment.delegate = this
    }

    private fun initAllFragments() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, menuFragment, "menu")
        fragmentTransaction.add(R.id.fragment_container, typesOfPlacesFragment, "types")
        fragmentTransaction.add(R.id.fragment_container, feedbackFragment, "feedback")
        fragmentTransaction.hide(typesOfPlacesFragment)
        fragmentTransaction.hide(feedbackFragment)
        fragmentTransaction.commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        baseMap = googleMap
        initKitchenerMap()
        setBoundariesAndZoom()
        enableLocationFunctionality()
        getCoordinatesOnLongClick()
        checkInfoWindowClick()
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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            baseMap.isMyLocationEnabled = true
        }
    }

    private fun setBoundariesAndZoom() {
        val boundaries = LatLngBounds(LatLng(34.476619, 32.163363), LatLng(35.847896, 34.838356))
        baseMap.setLatLngBoundsForCameraTarget(boundaries)
        baseMap.setMaxZoomPreference(15.0f)
        baseMap.setMinZoomPreference(7.0f)
        baseMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(initialLatitude, initialLongitude),
                initialZoomLevel
            )
        )
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
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    override fun replaceMenuFragments(menuId: String) {
        when (menuId) {
            "nav_types_of_places" -> {
                openFragment(typesOfPlacesFragment)
            }
            "nav_feedback" -> {
                openFragment(feedbackFragment)
            }
            "nav_about" -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
            "nav_main_menu" -> {
                openFragment(menuFragment)
            }
            "nav_opacity_slider" -> {
                toggleSlider()
            }
            else -> {
                Log.d(TAG, "Wrong string sent to function")
            }
        }
    }

    private fun openFragment(newFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(currentFragment)
        fragmentTransaction.show(newFragment)
        fragmentTransaction.commit()
        currentFragment = newFragment
    }

    private fun toggleSlider() {
        sliderVisible = !sliderVisible
        mapSlider.visibility = if (sliderVisible) GONE else VISIBLE
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (!hasInteractedWithSeekBar) {
                mapSlider.visibility = GONE
                nav_opacity_slider.isChecked = false
                sliderVisible = true
            }
            hasInteractedWithSeekBar = false
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
        mapsPresenter.addToQuery(filterType, filterValue)
        mapsPresenter.loadMarkers()
        showLoading()
    }

    override fun backToMenu() {
        openFragment(menuFragment)
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

    override fun setLocale(lang: String, reload: Boolean) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My Lang", lang)
        editor.apply()
        if (reload) {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun getCoordinatesOnLongClick() {
        baseMap.setOnMapLongClickListener(object : GoogleMap.OnMapLongClickListener {
            override fun onMapLongClick(latLng: LatLng) {
                longClickMarkers.forEach {
                    it.remove()
                }
                val marker = baseMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(latLng.latitude, latLng.longitude))
                        .title("Επιλεγμένο σημείο")
                        .snippet("Θέλετε να αφήσετε σχόλιο;")
                )
                marker.showInfoWindow()
                longClickMarkers.add(marker)
            }
        })
    }

    private fun checkInfoWindowClick() {
        baseMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker) {
                if (marker.title == "Επιλεγμένο σημείο") {
                    val intent = Intent(applicationContext, SendMailActivity::class.java)
                    intent.putExtra("latitude", marker.position.latitude)
                    intent.putExtra("longitude", marker.position.longitude)
                    startActivity(intent)
                }
            }
        })
    }

}
