package gr.hua.it21533.kitchenerMap.activities

import android.util.Log
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.networking.GoogleMapsApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MapsActivityPresenter(private val view: MapsActivityView) {

    private val query  = HashMap<String, Any>()

    fun addToQuery(key: String, value: Any) {
        query[key] = value
    }

    private val googleMapsApiServe by lazy {
        GoogleMapsApiService.create()
    }

    fun loadMarkers() {
        googleMapsApiServe.nearBySearch(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> view.displayMarkers(result.results) },
                { error -> Log.d("loadMarkers()","${error.message}") })
    }

}
