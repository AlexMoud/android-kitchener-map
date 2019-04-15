package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import android.location.Location
import android.support.v4.content.ContextCompat
import gr.hua.it21533.kitchenerMap.*
import gr.hua.it21533.kitchenerMap.networking.ApiModel
import gr.hua.it21533.kitchenerMap.fragments.*
import gr.hua.it21533.kitchenerMap.helpers.CustomMapTileProvider
import java.util.*

class MapsActivity : AppCompatActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback,
    MenuView,
    MapsActivityView {

    private val TAG = "MAPS_ACTIVITY"
    private lateinit var baseMap: GoogleMap
    private lateinit var kitchenerMapOverlay: TileOverlay
    private var sliderVisible = true
    private val initialLatitude: Double = 37.960
    private val initialLongitude: Double = 23.708
    private val initialZoomLevel = 16.0f
    private var markersList = ArrayList<Marker>()
    private lateinit var mapsPresenter: MapsActivityPresenter
    var queryMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initSlider()
        initSideMenu()
        queryMap["location"] = "$initialLatitude, $initialLongitude"
        mapsPresenter = MapsActivityPresenter(this, queryMap)
        mapsPresenter.loadMarkers()
    }

    override fun displayMarkers(markers: Array<ApiModel.Results>?) {
        markersList.forEach {
            it.remove()
        }
        markers?.forEach {
            var marker = baseMap.addMarker(MarkerOptions()
                .position(LatLng(it.geometry.location.lat, it.geometry.location.lng))
                .title(it.name ?: "No title given by the API")
                .snippet(it.vicinity ?: "No description given by the API")
                .alpha(0.8f))
            markersList.add(marker)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        baseMap = googleMap
        kitchenerMapOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(CustomMapTileProvider(assets)))
        baseMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initialLatitude, initialLongitude), initialZoomLevel))
        kitchenerMapOverlay.transparency = 1f
        baseMap.setOnMyLocationButtonClickListener(this)
        baseMap.setOnMyLocationClickListener(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            baseMap.isMyLocationEnabled = true
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
    }

    private fun initSlider() {
        mapSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                kitchenerMapOverlay.transparency = 1 - (progress.toFloat() / 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
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
                val fragment = TypesOfPlacesFragment()
                fragment.delegate = this
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    fragment
                ).commit()
            }
            "nav_feedback" -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    FeedbackFragment()
                ).commit()
            }
            "nav_about" -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    AboutFragment()
                ).commit()
            }
            "nav_main_menu" -> {
                backToMenu()
            }
            "nav_opacity_slider" -> {
                toggleSlider()
            }
            else -> {

            }
        }
    }

    private fun toggleSlider() {
        drawer_layout.closeDrawers()
        sliderVisible = !sliderVisible
        mapSlider.visibility = if (sliderVisible) GONE else VISIBLE
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
    }

    override fun backToMenu() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            MenuFragment()
        ).commit()
    }
}
