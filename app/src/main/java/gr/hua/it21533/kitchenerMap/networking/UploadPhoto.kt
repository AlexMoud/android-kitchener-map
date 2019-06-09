package gr.hua.it21533.kitchenerMap.networking

import com.google.android.gms.maps.model.LatLng
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Multipart

interface UploadPhoto {

    @Multipart
    @POST("https://gaia.hua.gr/tms/kitchener2/test/uploadPhoto")
    fun uploadPhoto(
        @Part description: String,
        @Part photo: MultipartBody.Part,
        @Part coordinates: LatLng): Call<ResponseBody>

    companion object {
        fun create(): UploadPhoto {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://gaia.hua.gr/tms/kitchener2/test/uploadPhoto")
                .build()

            return retrofit.create(UploadPhoto::class.java)
        }
    }
}