package gr.hua.it21533.kitchenerMap.networking

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.*

interface API {

    @GET
    fun textSearch(@Url url: String): Call<String>

    @GET
    fun getFeatureDetails(@Url url: String): Call<String>

    companion object {
        fun create(): API {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://gaia.hua.gr")
                .build()

            return retrofit.create(API::class.java)
        }
    }
}