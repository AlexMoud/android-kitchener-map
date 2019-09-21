package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
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
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.fragments.FeedbackFragment
import gr.hua.it21533.kitchenerMap.fragments.MenuFragment
import gr.hua.it21533.kitchenerMap.fragments.SearchFragment
import gr.hua.it21533.kitchenerMap.fragments.TypesOfPlacesFragment
import gr.hua.it21533.kitchenerMap.helpers.LayersHelper
import gr.hua.it21533.kitchenerMap.helpers.TileProviderFactory
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.networking.API
import gr.hua.it21533.kitchenerMap.networking.ApiModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_menu.*
import retrofit2.Call
import retrofit2.Response
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MapsActivity :
    AppCompatActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    OnMapReadyCallback,
    MenuView,
    MapsActivityView {

    private val REQUEST_LOCATION_PERMISSIONS = 1
    private val TAG = "MAPS_ACTIVITY"
    private lateinit var baseMap: GoogleMap
    private lateinit var kitchenerMapOverlay: TileOverlay
    private lateinit var kitchenerMapWMSOverlay: TileOverlay
    private lateinit var kitchenerMapWMSOverlayLegend: TileOverlay
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
    private val searchFragment = SearchFragment()
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
        mapsPresenter.addToTypesQuery("location", "$initialLatitude, $initialLongitude")
        mapsPresenter.addToTextSearchQuery("region", "cy")
        typesOfPlacesFragment.delegate = this
        searchFragment.delegate = this
        menuFragment.delegate = this
    }

    private fun checkForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSIONS
            )
        } else {
            enableLocationFunctionality()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var permissionsGranted = true
                    grantResults.forEach { i ->
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false
                        }
                    }
                    if (permissionsGranted) {
                        checkForPermissions()
                    }
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun initAllFragments() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, menuFragment, "menu")
        fragmentTransaction.add(R.id.fragment_container, typesOfPlacesFragment, "types")
        fragmentTransaction.add(R.id.fragment_container, searchFragment, "search")
        fragmentTransaction.add(R.id.fragment_container, feedbackFragment, "feedback")
        fragmentTransaction.hide(typesOfPlacesFragment)
        fragmentTransaction.hide(searchFragment)
        fragmentTransaction.hide(feedbackFragment)
        fragmentTransaction.commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        baseMap = googleMap
        try {
            baseMap.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        initKitchenerMap()
        setBoundariesAndZoom()
        checkForPermissions()
        getCoordinatesOnLongClick()
        checkInfoWindowClick()
    }

    private fun initKitchenerMap() {
        val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                val reversedY = (1 shl zoom) - y - 1
                val s = String.format(
                    "https://gaia.hua.gr/tms/kitchener_review/%d/%d/%d.jpg",
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

        val tileProviderWMS = TileProviderFactory.tileProvider
        kitchenerMapWMSOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProviderWMS))
        kitchenerMapWMSOverlay.transparency = 0f
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
                    .title(it.name ?: "No title")
                    .snippet(it.vicinity ?: "")
                    .alpha(0.8f)
            )
            markersList.add(marker)
        }
        Log.d(TAG,"${markersList.size}")
        if (markersList.size == 1) {
            baseMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        markersList[0].position.latitude,
                        markersList[0].position.longitude
                    ), initialZoomLevel
                )
            )
            drawer_layout.closeDrawers()
        }
        if(markersList.isEmpty()) {
            Toast.makeText(this, "No results" , Toast.LENGTH_SHORT).show()
        }
        hideLoading()
    }

    private fun removeMarkers() {
        markersList.forEach {
            it.remove()
        }
        markersList.clear()
    }

    private fun initSlider() {
        mapSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                kitchenerMapOverlay.transparency = 1 - (progress.toFloat() / 100)
                kitchenerMapWMSOverlay.transparency = kitchenerMapOverlay.transparency
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

    override fun replaceMenuFragments(menuItem: String) {
        when (menuItem) {
            "nav_types_of_places" -> {
                openFragment(typesOfPlacesFragment)
            }
            "nav_search" -> {
                openFragment(searchFragment)
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

    override fun didFilterChange() {

        kitchenerMapWMSOverlay.remove()
        kitchenerMapWMSOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(TileProviderFactory.tileProvider))
    }

    override fun onTextSearch(searchValue: String, filterType: String) {
        mapsPresenter.addToTextSearchQuery(filterType, searchValue)
        mapsPresenter.loadTextMarkers()
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
        setLocale(KitchenerMap.applicationContext().selectedLocale, false)
    }

    override fun setLocale(lang: String, reload: Boolean) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
        KitchenerMap.applicationContext().saveLocale(lang)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My Lang", lang)
        editor.apply()
        if (reload) {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun getCoordinatesOnLongClick() {
        baseMap.setOnMapLongClickListener { latLng ->
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
    }

    private fun checkInfoWindowClick() {
        baseMap.setOnInfoWindowClickListener { marker ->
            if (marker.title == "Επιλεγμένο σημείο") {
                val intent = Intent(applicationContext, SendMailActivity::class.java)
                intent.putExtra("latitude", marker.position.latitude)
                intent.putExtra("longitude", marker.position.longitude)
                startActivity(intent)
            }
        }
    }

}
