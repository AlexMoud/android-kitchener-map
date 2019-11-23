package gr.hua.it21533.kitchenerMap.helpers

import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.models.Gravoura
import gr.hua.it21533.kitchenerMap.networking.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GravouresHelper {

    var gravouresEn: Gravoura? = null
    var gravouresEl: Gravoura? = null

    init {
        load()
        loadFromAPI()
    }

    private fun load() {
        gravouresEn = KitchenerMap.applicationContext().getGraoures("gravouraEn")
        gravouresEl = KitchenerMap.applicationContext().getGraoures("gravouraEl")
    }

    private fun loadFromAPI() {
        val gravouresCall = API.create().getGravouraEn()
        gravouresCall.enqueue(object : Callback<Gravoura> {
            override fun onFailure(call: Call<Gravoura>, t: Throwable) {
            }

            override fun onResponse(call: Call<Gravoura>, response: Response<Gravoura>) {
                gravouresEn = response.body()
                response.body()?.let {
                    KitchenerMap.applicationContext().saveGravoura(it, "gravouraEn")
                }
            }
        })

        val gravouresCallEl = API.create().getGravouraEl()
        gravouresCallEl.enqueue(object : Callback<Gravoura> {
            override fun onFailure(call: Call<Gravoura>, t: Throwable) {
            }

            override fun onResponse(call: Call<Gravoura>, response: Response<Gravoura>) {
                gravouresEl = response.body()
                response.body()?.let {
                    KitchenerMap.applicationContext().saveGravoura(it, "gravouraEl")
                }
            }
        })
    }
}