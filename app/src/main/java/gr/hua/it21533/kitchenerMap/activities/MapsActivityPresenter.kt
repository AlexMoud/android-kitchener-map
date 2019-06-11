package gr.hua.it21533.kitchenerMap.activities

import android.util.Log
import gr.hua.it21533.kitchenerMap.interfaces.MapsActivityView
import gr.hua.it21533.kitchenerMap.networking.GoogleMapsApiService
import gr.hua.it21533.kitchenerMap.networking.UploadPhoto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class MapsActivityPresenter(private val view: MapsActivityView, private val query: HashMap<String, Any>) {

    private val TAG = "MAPS_ACTIVITY_PRESENTER"

    private val googleMapsApiServe by lazy {
        GoogleMapsApiService.create()
    }

    private val uploadPhotoServe by lazy {
        UploadPhoto.create()
    }

    fun loadMarkers() {
        googleMapsApiServe.nearBySearch(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> view.displayMarkers(result.results) },
                { error -> Log.d("loadMarkers()","${error.message}") })
    }

//    fun uploadPhoto(fileUri: String) {
//        val descriptionValue = "test description"
//
//        val descriptionPart = RequestBody.create(MultipartBody.FORM,  descriptionValue)
//        val filePart = RequestBody.create(
//            MediaType.parse(getContentResolver().getType(fileUri))),
//            FileUtils().getFile(this, fileUri)
//
//
//        uploadPhotoServe.uploadPhoto()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe()
//    }
}
