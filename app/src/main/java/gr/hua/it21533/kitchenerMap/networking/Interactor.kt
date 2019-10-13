package gr.hua.it21533.kitchenerMap.networking

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import gr.hua.it21533.kitchenerMap.helpers.LayersHelper
import gr.hua.it21533.kitchenerMap.models.Features
import gr.hua.it21533.kitchenerMap.models.SearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor {

    companion object {
        val shared: Interactor  = Interactor()
    }

    fun loadFeauteresOnLocation(location: LatLng, onSuccess: (List<Features>) -> Unit) {
        val latSW = location.latitude - 0.00001
        val lonSW = location.longitude - 0.00001
        val latNE = location.latitude + 0.00001
        val lonNE = location.longitude + 0.00001
        var baseUrl = "geoserver/ows?service=WMS&resource=02422ff9-9e60-430f-bbc5-bb5324359198" +
                "&version=1.3.0" +
                "&request=GetFeatureInfo" +
                "&FORMAT=image/png" +
                "&TRANSPARENT=true" +
                "&INFO_FORMAT=application/json" +
                "&FEATURE_COUNT=1000" +
                "&EXCEPTIONS=application/json" +
                "&QUERY_LAYERS=%t" +
                "&LAYERS=%s" +
                "&I=50" +
                "&J=50" +
                "&CRS=EPSG:4326" +
                "&STYLES=" +
                "&WIDTH=101" +
                "&HEIGHT=101" +
                "&BBOX=" + latSW + "," + lonSW + "," + latNE + "," + lonNE

        val layerString = LayersHelper.allLayersUrlEncoded
        baseUrl = baseUrl.replace("%t",layerString).replace("%s", layerString)


        val call = API.create().getFeatureDetails(baseUrl)
        call.enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let { body ->
                    val parsedBody = JsonParser().parse(body)
                    if (parsedBody.isJsonObject && parsedBody.asJsonObject.get("features").isJsonArray) {
                        val result = SearchResult(parsedBody.asJsonObject)
                        onSuccess(result.features)
                    }
                }
            }
        })
    }

    fun textSearchQuery(text: String, onSuccess: (SearchResult) -> Unit) {
        val baseUrl = "geoserver/ows?service=wfs" +
                "&version=2.0.0" +
                "&request=GetFeature" +
                "&typeName=kitchener:parametric_search_table2" +
                "&outputFormat=application/json" +
                "&viewparams=term:" +
                text +
                "&resource=02422ff9-9e60-430f-bbc5-bb5324359198" +
                "&srsName=EPSG:4326"

        val call = API.create().textSearch(baseUrl)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let { body ->
                    val parsedBody = JsonParser().parse(body)
                    if (parsedBody.isJsonObject && parsedBody.asJsonObject.get("features").isJsonArray) {
                        val result = SearchResult(parsedBody.asJsonObject)
                        onSuccess(result)
                    }
                }
            }
        })
    }
}