package gr.hua.it21533.kitchenerMap.activities

import android.util.Log
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.networking.GoogleMapsApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MapsActivityPresenter(private val view: MapsActivityView) {

    private val queryTypes  = HashMap<String, Any>()
    private val querySearch = HashMap<String, Any>()

    fun addToTypesQuery(key: String, value: Any) {
        queryTypes[key] = value
    }
    fun addToTextSearchQuery(key: String, value: Any) {
        querySearch[key] = value
    }

    private val googleMapsApiServe by lazy {
        GoogleMapsApiService.create()
    }

    fun loadTypesMarkers() {
        googleMapsApiServe.nearBySearch(queryTypes)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> view.displayMarkers(result.results) },
                { error -> Log.d("loadMarkers()","${error.message}") })
    }


    fun loadTextMarkers() {
        googleMapsApiServe.textSearch(querySearch)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> view.displayMarkers(result.results) },
                { error -> Log.d("loadMarkers()","${error.message}") })
    }

}
