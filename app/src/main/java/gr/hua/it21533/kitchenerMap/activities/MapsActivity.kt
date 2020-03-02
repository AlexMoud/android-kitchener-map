package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.fragments.*
import gr.hua.it21533.kitchenerMap.helpers.*
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.models.Features
import gr.hua.it21533.kitchenerMap.models.Gravoura
import gr.hua.it21533.kitchenerMap.models.GravouraInfoWindowData
import gr.hua.it21533.kitchenerMap.models.LayerX
import gr.hua.it21533.kitchenerMap.networking.ApiModel
import gr.hua.it21533.kitchenerMap.networking.Interactor
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.custom_info_window.view.*
import kotlinx.android.synthetic.main.fragment_menu.*
import android.net.ConnectivityManager
import gr.hua.it21533.kitchenerMap.networking.ConnectionChangeReceiver
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : BaseActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    OnMapReadyCallback,
    MenuView,
    MapsActivityView,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnInfoWindowClickListener,ConnectionChangeReceiver.ConnectionChangeInterface {

    private var transparency: Float = 0f
    private val REQUEST_LOCATION_PERMISSIONS = 1
    private val TAG = "MAPS_ACTIVITY"

    private lateinit var baseMap: GoogleMap
//    private var mapOverlays: ArrayList<TileOverlay> = ArrayList()
    private var kitchenerMapOverlay: TileOverlay? = null
    private var kitchenerMapLeukosiaOverlay: TileOverlay? = null
    private var kitchenerMapLimasolOverlay: TileOverlay? = null
    private var modernMapOverlayA: TileOverlay? = null
    private var modernMapOverlayB: TileOverlay? = null
    private lateinit var kitchenerMapWMSOverlay: TileOverlay
    private lateinit var tileProviderWMS: WMSTileProvider

    private var selectedPolyline: Polyline? = null
    private var selectedPolygon: Polygon? = null

    private var sliderVisible = true

    private var markersList = ArrayList<Marker>()
    private var longClickMarker: Marker? = null
    private var gravouraMarkers = ArrayList<Marker>()
    private var receiver = ConnectionChangeReceiver()
    private var hasInteractedWithSeekBar = false
    private var initialLatitude: Double = 35.17
    private var initialLongitude: Double = 33.36
    private val initialZoomLevel = 10.0f

    private val handler = Handler()

    private val typesOfPlacesFragment = TypesOfPlacesFragment()
    private val searchFragment = SearchFragment()
    private val menuFragment = MenuFragment()
    private val feedbackFragment = FeedbackFragment()
    private var currentFragment: Fragment = menuFragment
    private val termsFragment = TermsFragment()
    private val policyFragment = PolicyFragment()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        LayersHelper.reloadLayers()
        initSlider()
        initSideMenu()
        loadLocale()
        initAllFragments()
        showNoInternetIfNeeded()
        registerConnectionReceiver()
        typesOfPlacesFragment.delegate = this
        searchFragment.delegate = this
        menuFragment.delegate = this
        clear.setOnClickListener {
            clearFilters()
            clearMarkers()
            clearGravouraMarkers()
            clearTextSearchResults()
            clearBackgroundBaseMaps()
        }
        scaleView.metersAndMiles()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    override fun finish() {
        super.finish()
        unregisterReceiver(receiver)

    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            if (currentFragment != menuFragment) {
                backToMenu()
                return
            }
            drawer_layout.closeDrawer(Gravity.START)
            return
        }
        if (longClickMarker?.isInfoWindowShown == true) {
            longClickMarker?.hideInfoWindow()
            return
        }
        if (gravouraMarkers.isNotEmpty()) {
            gravouraMarkers.forEach {
                if (it.isInfoWindowShown) {
                    it.hideInfoWindow()
                    return
                }
            }
        }
        super.onBackPressed()
    }

    private fun checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSIONS)
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
        fragmentTransaction.add(R.id.fragment_container, termsFragment, "terms")
        fragmentTransaction.add(R.id.fragment_container, policyFragment, "policy")
        fragmentTransaction.hide(typesOfPlacesFragment)
        fragmentTransaction.hide(searchFragment)
        fragmentTransaction.hide(feedbackFragment)
        fragmentTransaction.hide(termsFragment)
        fragmentTransaction.hide(policyFragment)
        fragmentTransaction.commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        baseMap = googleMap
        try {
            baseMap.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        baseMap.setOnCameraMoveStartedListener(this)
        baseMap.setOnInfoWindowClickListener(this)
        initKitchenerMap()
        setBoundariesAndZoom()
        checkForPermissions()
        getCoordinatesOnLongClick()
        baseMap.uiSettings.isZoomControlsEnabled = true
        baseMap.setPadding(4,4,4,40)
    }

    private fun setKitchenerMap() {
        val tileProvider: CachingTileProvider = object : CachingTileProvider(256, 256, this) {
            override fun getTileUrl(x: Int, y: Int, z: Int): String {
                val reversedY = (1 shl z) - y - 1
                return String.format("https://gaia.hua.gr/tms/kitchener_review/%d/%d/%d.jpg", z, x, reversedY)
            }
        }
        kitchenerMapOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        kitchenerMapOverlay?.transparency = transparency
    }


    private fun setNikosiaMap() {
        val tileProvider: CachingTileProvider = object : CachingTileProvider(256, 256, this) {
            override fun getTileUrl(x: Int, y: Int, z: Int): String {
                val reversedY = (1 shl z) - y - 1
                return String.format("https://gaia.hua.gr/tms/kitchener_nicosia_plan/%d/%d/%d.png", z, x, reversedY)
            }
        }
        kitchenerMapLeukosiaOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        kitchenerMapLeukosiaOverlay?.transparency = transparency
        updateNikosiaLayerLevel()
    }

    private fun setLimasolMap() {
        val tileProvider: CachingTileProvider = object : CachingTileProvider(256, 256, this) {
            override fun getTileUrl(x: Int, y: Int, z: Int): String {
                val reversedY = (1 shl z) - y - 1
                return String.format("https://gaia.hua.gr/tms/kitchener_limassol_plan/%d/%d/%d.png", z, x, reversedY)
            }
        }
        kitchenerMapLimasolOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        kitchenerMapLimasolOverlay?.transparency = transparency
        updateLimasolLayerLevel()
    }

    private fun setModernMapA() {
        val tileProvider: CachingTileProvider = object : CachingTileProvider(256, 256, this) {
            override fun getTileUrl(x: Int, y: Int, z: Int): String {
                return String.format("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/%d/%d/%d", z, y, x)
            }
        }
        modernMapOverlayA = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        modernMapOverlayA?.transparency = transparency
    }

    private fun setModernMapB() {
        val tileProvider: CachingTileProvider = object : CachingTileProvider(256, 256, this) {
            override fun getTileUrl(x: Int, y: Int, z: Int): String {
                return String.format("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/%d/%d/%d", z, y, x)
            }
        }
        modernMapOverlayB = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        modernMapOverlayB?.transparency = transparency
    }

    private fun initKitchenerMap() {
        setKitchenerMap()
        tileProviderWMS = TileProviderFactory.tileProvider
        kitchenerMapWMSOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProviderWMS))
        kitchenerMapWMSOverlay.transparency = 0f
        kitchenerMapWMSOverlay.zIndex = 50f

        baseMap.setOnMapClickListener(this)

        baseMap.setOnCameraMoveListener {
            updateNikosiaLayerLevel()
            updateLimasolLayerLevel()
            scaleView.update(baseMap.cameraPosition.zoom, baseMap.cameraPosition.target.latitude)
        }
        baseMap.setOnCameraIdleListener {
            updateNikosiaLayerLevel()
            updateLimasolLayerLevel()
            scaleView.update(baseMap.cameraPosition.zoom, baseMap.cameraPosition.target.latitude)
        }
    }

    private  fun registerConnectionReceiver() {
        //connectivity manager
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver, intentFilter)
        ConnectionChangeReceiver.register(this)
    }
    private fun updateNikosiaLayerLevel() {
        if (baseMap.cameraPosition.zoom <= 15) {
            kitchenerMapLeukosiaOverlay?.transparency = 1f
        } else {
            kitchenerMapLeukosiaOverlay?.transparency = transparency
        }
    }

    private fun updateLimasolLayerLevel() {
        if (baseMap.cameraPosition.zoom <= 15) {
            kitchenerMapLimasolOverlay?.transparency = 1f
        } else {
            kitchenerMapLimasolOverlay?.transparency = transparency
        }
    }

    private fun enableLocationFunctionality() {
        baseMap.setOnMyLocationButtonClickListener(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            baseMap.isMyLocationEnabled = true
        }
    }

    private fun setBoundariesAndZoom() {
        val boundaries = LatLngBounds(LatLng(34.541328, 32.211591), LatLng(35.721089, 34.621623))
        baseMap.setLatLngBoundsForCameraTarget(boundaries)
        baseMap.setMaxZoomPreference(17.99f)
        baseMap.setMinZoomPreference(7.0f)

        baseMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(initialLatitude, initialLongitude), initialZoomLevel))

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                if (boundaries.contains(userLatLng)) {
                    baseMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, initialZoomLevel))
                }
            }

        }
    }

    private fun addMarkersOfGravoura() {
        gravouraMarkers.clear()
        val isGreek = KitchenerMap.applicationContext().selectedLocale == "el"
        val gravoures: Gravoura? = if (isGreek) {
            GravouresHelper.gravouresEl
        } else {
            GravouresHelper.gravouresEn
        }
        baseMap.setInfoWindowAdapter(CustomInfoWindowGoogleMap(this))
        baseMap.setOnInfoWindowClickListener(this)
        gravoures?.features?.forEach { feature ->
            if (feature.geometry.coordinates.size == 2) {
                val markerOptions = MarkerOptions()
                    .position(LatLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0]))
                    .title(feature.properties.getNameCrpped())
                    .icon(KitchenerMap.applicationContext().bitmapDescriptorFromVector(this, R.drawable.ic_place_black_24dp))
                val infoWindowData = GravouraInfoWindowData()
                infoWindowData.name = feature.properties.getNameCrpped()
                infoWindowData.image = "https://gaia.hua.gr" + feature.properties.thumbnail
                infoWindowData.link = "https://gaia.hua.gr" + feature.properties.link
                Picasso.get().load(infoWindowData.image).into(ImageView(this))
                val marker = baseMap.addMarker(markerOptions)
                marker.tag = infoWindowData
                gravouraMarkers.add(marker)
            }
        }
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
                transparency = 1 - (progress.toFloat() / 100)
                kitchenerMapOverlay?.transparency = transparency
                kitchenerMapWMSOverlay.transparency = transparency
                kitchenerMapLimasolOverlay?.transparency = transparency
                modernMapOverlayA?.transparency = transparency
                modernMapOverlayB?.transparency = transparency
                updateNikosiaLayerLevel()
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
                typesOfPlacesFragment.refresh()
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
            "terms" -> {
                openFragment(termsFragment)
            }
            "policy" -> {
                openFragment(policyFragment)
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

    override fun didSelectMapOverlay(layer: LayerX, position: Int) {
        when(position) {
            0 -> {
                if (kitchenerMapOverlay == null) {
                    setKitchenerMap()
                } else {
                    kitchenerMapOverlay?.remove()
                    kitchenerMapOverlay = null
                }
            }
            1 -> {
                if (kitchenerMapLeukosiaOverlay == null) {
                    setNikosiaMap()
                } else {
                    kitchenerMapLeukosiaOverlay?.remove()
                    kitchenerMapLeukosiaOverlay = null
                }
            }
            2 -> {
                if (kitchenerMapLimasolOverlay == null) {
                    setLimasolMap()
                } else {
                    kitchenerMapLimasolOverlay?.remove()
                    kitchenerMapLimasolOverlay = null
                }
            }
            4 -> {
                if (modernMapOverlayA == null) {
                    setModernMapA()
                } else {
                    modernMapOverlayA?.remove()
                    modernMapOverlayA = null
                }
            }
            5 -> {
                if (modernMapOverlayB == null) {
                    setModernMapB()
                } else {
                    modernMapOverlayB?.remove()
                    modernMapOverlayB = null
                }
            }
            else -> {

            }
        }
        didFilterChange()
    }

    override fun didFilterChange() {
        kitchenerMapWMSOverlay.remove()
        kitchenerMapWMSOverlay = baseMap.addTileOverlay(TileOverlayOptions().tileProvider(TileProviderFactory.tileProvider))
    }

    private fun clearFilters() {
        TileProviderFactory.layers.clear()
        if (kitchenerMapOverlay == null) {
            setKitchenerMap()
        }
        kitchenerMapLimasolOverlay?.remove()
        kitchenerMapLimasolOverlay = null
        kitchenerMapLeukosiaOverlay?.remove()
        kitchenerMapLeukosiaOverlay = null
        modernMapOverlayA?.remove()
        modernMapOverlayA = null
        modernMapOverlayB?.remove()
        modernMapOverlayB = null
        didFilterChange()
        typesOfPlacesFragment.clear()
    }

    private fun clearMarkers() {
        longClickMarker?.remove()
        longClickMarker = null
    }

    private fun clearTextSearchResults() {
        hideInfoWindow()
        selectedPolyline?.remove()
        selectedPolygon?.remove()
    }

    private fun clearBackgroundBaseMaps() {
        menuFragment?.nav_opacity_slider?.isChecked = false
        mapSlider.progress = 100
        sliderVisible = true
        mapSlider.visibility = GONE
    }

    private fun clearGravouraMarkers() {
        menuFragment.deselectGravoures()
    }

    override fun showLoader() {
        showLoading()
    }

    override fun hideLoader() {
        hideLoading()
    }

    override fun zoomOnPlace(features: Features, location: LatLng?) {
        selectedPolyline?.remove()
        selectedPolygon?.remove()
        val builder = LatLngBounds.Builder()
        val points = ArrayList<LatLng>()
        features.geometry.point?.let {
            points.add(it)
        }
        features.geometry.points?.let {
            points.addAll(it)
        }
        val polyline = PolylineOptions().clickable(true).width(20f).color(ContextCompat.getColor(applicationContext, R.color.colorAccent)).zIndex(200f)
        points.forEach {
            polyline.add(it)
            builder.include(it)
        }
        selectedPolyline = baseMap.addPolyline(polyline)

        drawer_layout.closeDrawer(GravityCompat.START)


        val bounds = builder.build()

        if (points.size > 1) {
            baseMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
        } else if (points.size == 1) {
            val polygonOptions = PolygonOptions()
                .clickable(true)
                .strokeWidth(20f)
                .strokeJointType(JointType.ROUND)
                .fillColor(ContextCompat.getColor(applicationContext, R.color.colorAccentAlpha))
                .strokeColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                .zIndex(200f)
            polygonOptions.add(LatLng(points[0].latitude - 0.0005, points[0].longitude - 0.001))
            polygonOptions.add(LatLng(points[0].latitude + 0.0005, points[0].longitude - 0.001))
            polygonOptions.add(LatLng(points[0].latitude + 0.0005, points[0].longitude + 0.001))
            polygonOptions.add(LatLng(points[0].latitude - 0.0005, points[0].longitude + 0.001))

            selectedPolygon = baseMap.addPolygon(polygonOptions)
            baseMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.first(), 14f))
            showInfoWindow(features)

            baseMap.setOnPolygonClickListener {
                showInfoWindow(features)
            }
        }

        baseMap.setOnPolylineClickListener {
            showInfoWindow(features)
        }
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
            clearFilters()
            finish()
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    override fun gravouraSelected() {
        addMarkersOfGravoura()
    }

    override fun gravouraDeselected() {
        gravouraMarkers.forEach {
            it.remove()
        }
    }

    private fun getCoordinatesOnLongClick() {
        baseMap.setOnMapLongClickListener { latLng ->
            longClickMarker?.remove()
            longClickMarker = null
            val isGreek = KitchenerMap.applicationContext().selectedLocale == "el"
            val title = if (isGreek) "Επιλεγμένο σημείο" else "Selected point"
            val snipet = if (isGreek) "Θέλετε να αφήσετε σχόλιο;" else "would you like to add a comment?"
            val markerOptions = MarkerOptions()
                .position(LatLng(latLng.latitude, latLng.longitude))
                .title(title)
                .snippet(snipet)
            val infoWindowData = GravouraInfoWindowData()
            infoWindowData.name = title
            infoWindowData.snipet = snipet
            longClickMarker = baseMap.addMarker(markerOptions)
            longClickMarker?.tag = infoWindowData
            longClickMarker?.showInfoWindow()
        }
    }

    override fun onMapClick(p0: LatLng?) {
        p0?.let {
            loadFeaturesOnLocation(it)
        }
    }

    private fun loadFeaturesOnLocation(location: LatLng) {
        Interactor.shared.loadFeauteresOnLocation(location) { featuresArray ->
            if (featuresArray.isNotEmpty()) {
                zoomOnPlaceTapped(featuresArray.first(), location, true)
            } else {
                hideInfoWindow()
            }
        }
    }

    private fun zoomOnPlaceTapped(features: Features, location: LatLng, isOnMapClick: Boolean) {
        var latlng = features.geometry.point ?: features.geometry.points?.first()
        if (latlng == null || isOnMapClick) {
            latlng = location
        }
        baseMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, baseMap.cameraPosition.zoom))
        drawer_layout.closeDrawer(GravityCompat.START)
        showInfoWindow(features)
    }

    private fun showInfoWindow(feature: Features) {
        fakeView.visibility = VISIBLE
        val isGreek = KitchenerMap.applicationContext().selectedLocale == "el"
        custom_window.title.text = if (!isGreek) {
            feature.properties?.values?.nameEN ?: "No Name"
        } else {
            feature.properties?.values?.nameEL ?: "Χωρίς Όνομα"
        }
        if (!isGreek) {
            setData(custom_window.category, feature.properties?.values?.categoryEN, null)
        } else {
            setData(custom_window.category, feature.properties?.values?.categoryEL, null)
        }

        setData(custom_window.poi_name, feature.poiProperties?.name, custom_window.name_layout)
        setData(custom_window.nameGreek, feature.poiProperties?.nameGreek, custom_window.greek_layout)
        setData(custom_window.nameRoman, feature.poiProperties?.nameRoman, custom_window.english_layout)
        setData(custom_window.second_name, feature.poiProperties?.secondName, custom_window.second_name_layout)
        setData(custom_window.district, feature.poiProperties?.district, custom_window.district_layout)
        custom_window.visibility = VISIBLE
        custom_window.cancel.setOnClickListener {
            hideInfoWindow()
        }
    }

    private fun setData(textView: TextView, data: String?, parentLayout: View?) {
        textView.text = data
        if (textView.text == null || textView.text == "") {
            parentLayout?.visibility = GONE
        }else {
            parentLayout?.visibility = VISIBLE
        }
    }

    private fun hideInfoWindow() {
        fakeView.visibility = GONE
        custom_window.visibility = GONE
    }

    override fun onCameraMoveStarted(p0: Int) {
        if (p0 == 1) {
            hideInfoWindow()
        }
    }

    override fun onInfoWindowClick(p0: Marker?) {
        if (p0 != null && longClickMarker == p0) {
            val intent = Intent(applicationContext, SendMailActivity::class.java)
            intent.putExtra("latitude", p0.position.latitude)
            intent.putExtra("longitude", p0.position.longitude)
            startActivity(intent)
        } else {
            val info = p0?.tag as GravouraInfoWindowData?
            info?.link?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            }
        }
    }

    private fun showNoInternetIfNeeded() {
        if (!KitchenerMap.isNetworkAvailable()) {
            noconnectionmsg.visibility = View.VISIBLE
        }
    }

    override fun connectionChanged() {
        if (KitchenerMap.isNetworkAvailable()) {
            noconnectionmsg.visibility = View.GONE
        } else {
            noconnectionmsg.visibility = View.VISIBLE
        }
    }
}
