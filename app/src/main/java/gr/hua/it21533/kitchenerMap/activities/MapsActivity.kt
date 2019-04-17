package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
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
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.fragments.AboutFragment
import gr.hua.it21533.kitchenerMap.fragments.FeedbackFragment
import gr.hua.it21533.kitchenerMap.fragments.MenuFragment
import gr.hua.it21533.kitchenerMap.fragments.TypesOfPlacesFragment
import gr.hua.it21533.kitchenerMap.helpers.CustomMapTileProvider
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.networking.ApiModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_menu.*
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
    private var hasInteractedWithSeekBar = false
    var queryMap = HashMap<String, Any>()
    private val handler = Handler()

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
        hideLoading()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        baseMap = googleMap
        kitchenerMapOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(CustomMapTileProvider(assets)))
        baseMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initialLatitude, initialLongitude), initialZoomLevel))
        kitchenerMapOverlay.transparency = 1f
        baseMap.setOnMyLocationButtonClickListener(this)
        baseMap.setOnMyLocationClickListener(this)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            baseMap.isMyLocationEnabled = true
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {}

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
        if(hasInteractedWithSeekBar) hasInteractedWithSeekBar = false
        mapSlider.visibility = if (sliderVisible) GONE else VISIBLE
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if(!hasInteractedWithSeekBar) {
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
}
