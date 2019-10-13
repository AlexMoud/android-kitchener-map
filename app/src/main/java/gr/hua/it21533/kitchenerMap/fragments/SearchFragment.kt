package gr.hua.it21533.kitchenerMap.fragments

import gr.hua.it21533.kitchenerMap.models.SearchResult
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.JsonParser
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import gr.hua.it21533.kitchenerMap.adapters.POIAdapter
import gr.hua.it21533.kitchenerMap.helpers.LayersHelper
import gr.hua.it21533.kitchenerMap.helpers.TileProviderFactory
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.models.Features
import gr.hua.it21533.kitchenerMap.models.MapLayer
import gr.hua.it21533.kitchenerMap.networking.API
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment(), SearchView.OnQueryTextListener, Callback<String> {


    var delegate: MenuView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onStart() {
        super.onStart()
        backToMenu.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_main_menu")
        }

        search_bar.setOnQueryTextListener(this)
        context?.let {
            val adapter = POIAdapter(it, ArrayList()) { features ->
                loadFeatureDetails(features)
            }
            recycler.layoutManager = LinearLayoutManager(it)
            recycler.adapter = adapter
        }
    }

    private fun loadFeatureDetails(features: Features) {
        delegate?.zoomOnPlace(features)
    }

    private fun reloadRecycler(features: List<Features>) {
        (recycler.adapter as? POIAdapter)?.data = features
        recycler.adapter?.notifyDataSetChanged()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        p0?.let {
            if (it.length > 2) {
                val baseUrl = "geoserver/ows?service=wfs" +
                        "&version=2.0.0" +
                        "&request=GetFeature" +
                        "&typeName=kitchener:parametric_search_table2" +
                        "&outputFormat=application/json" +
                        "&viewparams=term:" +
                        it +
                        "&resource=02422ff9-9e60-430f-bbc5-bb5324359198" +
                        "&srsName=EPSG:4326"

                val call = API.create().textSearch(baseUrl)
                call.enqueue(this)
            }else {
                clearSearch()
            }
        }

        return true
    }

    private fun clearSearch() {
        reloadRecycler(ArrayList())
        subtitle.text = activity?.resources?.getString(R.string.search_subtitle)
    }

    private fun refreshSearch(searchResult: SearchResult) {
        var s = activity?.resources?.getString(R.string.search_result_subtitle)
        s = s?.replace("$", searchResult.numberReturned.toString(), false)
        subtitle.text = s

        reloadRecycler(searchResult.features)
    }

    override fun onFailure(call: Call<String>, t: Throwable) {
        return
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        val json = JsonParser().parse(response.body()).asJsonObject
        val result = SearchResult(json)
        refreshSearch(result)
    }
}

