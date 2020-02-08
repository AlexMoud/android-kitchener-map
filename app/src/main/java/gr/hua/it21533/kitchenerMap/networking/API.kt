package gr.hua.it21533.kitchenerMap.networking

import gr.hua.it21533.kitchenerMap.models.Gravoura
import gr.hua.it21533.kitchenerMap.models.HuaSettings
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface API {

    @Headers("X-Application-Request-Origin: mobileSet=mobileAPIuser1&mobileSubSet=OesomEtaT")
    @GET
    fun textSearch(@Url url: String): Call<String>

    @Headers("X-Application-Request-Origin: mobileSet=mobileAPIuser1&mobileSubSet=OesomEtaT")
    @GET
    fun getFeatureDetails(@Url url: String): Call<String>

    @Headers("X-Application-Request-Origin: mobileSet=mobileAPIuser1&mobileSubSet=OesomEtaT")
    @GET("en/coastal_cyprus/visualrepresentations/json")
    fun getGravouraEn(): Call<Gravoura>

    @Headers("X-Application-Request-Origin: mobileSet=mobileAPIuser1&mobileSubSet=OesomEtaT")
    @GET("el/coastal_cyprus/visualrepresentations/json")
    fun getGravouraEl(): Call<Gravoura>

    @Headers("X-Application-Request-Origin: mobileSet=mobileAPIuser1&mobileSubSet=OesomEtaT")
    @GET("/kitchener_review/js/settings_web.json")
    fun getBaseMaps(): Call<HuaSettings>

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