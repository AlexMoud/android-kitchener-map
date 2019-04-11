package gr.hua.it21533.kitchenerMap.activities

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
import gr.hua.it21533.kitchenerMap.api.ApiModel
import gr.hua.it21533.kitchenerMap.helpers.CustomMapTileProvider
import gr.hua.it21533.kitchenerMap.api.GoogleMapsApiService
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.fragments.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, FilteringListener {

    private val TAG = "MAPS_ACTIVITY"
    private lateinit var mMap: GoogleMap
    private var kitchenerMapOverlay: TileOverlay? = null
    private var sliderVisible = true
    private val initialLatitute: Double = 37.960
    private val initialLongitude: Double = 23.708
    private val initialZoomLevel = 16.0f

    var queryMap = HashMap<String, Any>()
    private var disposable: Disposable? = null

    private val GoogleMapsApiServe by lazy {
        GoogleMapsApiService.create()
    }

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
        mMap = googleMap
        kitchenerMapOverlay = mMap.addTileOverlay(
            TileOverlayOptions().tileProvider(
                CustomMapTileProvider(
                    assets
                )
            )
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initialLatitute, initialLongitude), initialZoomLevel))
        kitchenerMapOverlay?.transparency = 1f
    }

    private fun initSlider() {
        mapSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressValue = progress.toFloat() / 100
                kitchenerMapOverlay?.transparency = 1 - progressValue
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
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
                //add listener/delegate the activity must implement the interface and non optional functions
                val fragment = TypesOfPlacesFragment()
                fragment.delegate = this
                //or
                fragment.onFilterSelected = {
                    //TODO: call function and do something with it: TypesModel object

                }
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    fragment
                )
                    .commit()
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
        queryMap.put("location", "$initialLatitute, $initialLongitude")
        disposable = GoogleMapsApiServe.nearBySearch(queryMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> addMarkers(result.results) },
                { error -> Log.d(TAG, "${error.message}") })
    }

    fun addMarkers(results: Array<ApiModel.Results>) {
        results.forEach {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.geometry.location.lat, it.geometry.location.lng))
                    .title(it.name)
                    .snippet(it.vicinity)
            )
        }
    }

    override fun didSelectFilter(filter: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didDeselect(filter: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
