package gr.hua.it21533.kitchenerMap.api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface GoogleMapsApiService {

    @GET("maps/api/place/nearbysearch/json?key=AIzaSyCQGxkJxY_7np6G7qEu9ylkpMG72gylgG8&radius=1500")
    fun nearBySearch(
        @QueryMap queryMap: HashMap<String, Any>): Observable<ApiModel.Result>


    companion object {
        fun create(): GoogleMapsApiService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build()

            return retrofit.create(GoogleMapsApiService::class.java)
        }
    }
}