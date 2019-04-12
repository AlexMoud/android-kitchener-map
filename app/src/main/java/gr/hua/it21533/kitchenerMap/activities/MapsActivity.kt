package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps.*
import android.location.Location
import android.support.v4.content.ContextCompat
import android.widget.Toast
import gr.hua.it21533.kitchenerMap.*
import gr.hua.it21533.kitchenerMap.api.ApiModel
import gr.hua.it21533.kitchenerMap.api.GoogleMapsApiService
import gr.hua.it21533.kitchenerMap.fragments.*
import gr.hua.it21533.kitchenerMap.helpers.CustomMapTileProvider
import java.util.*

class MapsActivity : AppCompatActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback,
    FilteringListener {

    private val TAG = "MAPS_ACTIVITY"
    private lateinit var baseMap: GoogleMap
    private lateinit var kitchenerMapOverlay: TileOverlay
    private var sliderVisible = true
    private val initialLatitude: Double = 37.960
    private val initialLongitude: Double = 23.708
    private val initialZoomLevel = 16.0f
    private var disposable: Disposable? = null
    private var markersList = ArrayList<Marker>()

    private val googleMapsApiServe by lazy {
        GoogleMapsApiService.create()
    }

    var queryMap = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initSlider()
        initSideMenu()
        searchForPlaces()
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
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location: $location", Toast.LENGTH_LONG).show()
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
                //or
//                fragment.onFilterSelected = {
//
//                }
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
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    MenuFragment()
                ).commit()
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

    fun searchForPlaces() {
        // add query strings to map and call api service with query map
        queryMap["location"] = "$initialLatitude, $initialLongitude"
        disposable = googleMapsApiServe.nearBySearch(queryMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> addMarkers(result.results) },
                { error -> Log.d(TAG,"${error.message}") })
    }

    private fun addMarkers(results: Array<ApiModel.Results>) {
        markersList.forEach {
            it.remove()
        }
        results.forEach {
            var marker = baseMap.addMarker(MarkerOptions()
                .position(LatLng(it.geometry.location.lat, it.geometry.location.lng))
                .title(it.name ?: "No title given by the API")
                .snippet(it.vicinity ?: "No description given by the API")
                .alpha(0.8f))

            markersList.add(marker)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }


    override fun didSelectFilter(filter: String) {
        Log.d(TAG,"$filter")
    }

    override fun didDeselect(filter: String) {
        Log.d(TAG,"$filter")
    }
}
