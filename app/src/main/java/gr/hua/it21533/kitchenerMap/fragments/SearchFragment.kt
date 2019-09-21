package gr.hua.it21533.kitchenerMap.fragments

import SearchResult
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonParser
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import gr.hua.it21533.kitchenerMap.helpers.LayersHelper
import gr.hua.it21533.kitchenerMap.interfaces.MenuView
import gr.hua.it21533.kitchenerMap.models.Base
import gr.hua.it21533.kitchenerMap.networking.API
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET


class SearchFragment : Fragment(), SearchView.OnQueryTextListener, Callback<String> {


    private val TAG = "SEARCH_FRAGMENT"
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
                        "&srsName=EPSG:3857"

                val call = API.create().textSearch(baseUrl)
                call.enqueue(this)
            }else {
                //TODO: clear recycler
            }
        }

        return true
    }

    override fun onFailure(call: Call<String>, t: Throwable) {
        return
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        val json = JsonParser().parse(response.body()).asJsonObject
        print(json)
        val result: SearchResult = Gson().fromJson(json, SearchResult::class.java)
        //TODO: refresh Recycler
        print(result)
    }
}

