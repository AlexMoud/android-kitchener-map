package gr.hua.it21533.kitchenerMap.helpers

import android.util.Log
import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.models.HuaSettings
import gr.hua.it21533.kitchenerMap.models.MapLayer
import gr.hua.it21533.kitchenerMap.models.MapLayerParent
import gr.hua.it21533.kitchenerMap.networking.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LayersHelper {

    var data: MutableList<MapLayerParent>
    var settingsData: HuaSettings? = null
    var allLayersUrlEncoded: String = ""
    var mapOverlays: ArrayList<String> = ArrayList()

    init {
        data = getLayerParents()
        load()
    }

    private fun load() {
        val call = API.create().getBaseMaps()
        call.enqueue(object : Callback<HuaSettings> {

            override fun onFailure(call: Call<HuaSettings>, t: Throwable) {
                Log.e("base maps json", t.localizedMessage)
            }

            override fun onResponse(call: Call<HuaSettings>, response: Response<HuaSettings>) {
                settingsData = response.body()
            }
        })
    }

    fun reloadLayers() {
        data = getLayerParents()
    }

    private fun getLayerParents() : MutableList<MapLayerParent> {
        val result = ArrayList<MapLayerParent>()
        val isEnglish = KitchenerMap.applicationContext().selectedLocale == "en"
        mapOverlays.clear()
        allLayersUrlEncoded = ""

        settingsData?.baseMapGroups?.forEach { wmsMaps ->
//            if (wmsMaps.id != 200) {
                val mapLayers = ArrayList<MapLayer>()
                wmsMaps.layers.forEach { layer ->
                    if (layer.type != "OSM") {
                        if (isEnglish) {
                            mapLayers.add(MapLayer(layer.name.en, false, layer, wmsMaps.userOrder))
                        } else {
                            mapLayers.add(MapLayer(layer.name.el, false, layer, wmsMaps.userOrder))
                        }
                        mapOverlays.add(layer.src)
                    }
                }
                if (isEnglish) {
                    result.add(MapLayerParent(wmsMaps.name.en, mapLayers))
                } else {
                    result.add(MapLayerParent(wmsMaps.name.el, mapLayers))
                }
//            }
        }

        settingsData?.layerGroups?.forEach { base ->
            if (base.type != "WMSContainer") {
                val layers = ArrayList<MapLayer>()
                base.layers.forEach { lay ->
                    if (isEnglish) {
                        layers.add(MapLayer(lay.name.en, false, lay, 0))
                    } else {
                        layers.add(MapLayer(lay.name.el, false, lay, 0))
                    }
                    allLayersUrlEncoded += lay.src + ","
                }
                if (isEnglish) {
                    result.add(MapLayerParent(base.name.en, layers))
                } else {
                    result.add(MapLayerParent(base.name.el, layers))
                }
            }
        }

        allLayersUrlEncoded = "kitchener:cover_group," +
                "kitchener:river_network_group," +
                "kitchener:road_net_group," +
                "kitchener:telegraph," +
                "kitchener:borders_group," +
                "kitchener:km_distance," +
                "kitchener:point_data_group," +
                "kitchener:text_town_group," +
                "kitchener:toponym_po"
        allLayersUrlEncoded.removeSuffix(",")
        return result
    }

    fun getLayers() : String {
        var layerString = ""
        TileProviderFactory.layers.forEach {
            layerString += "$layerString$it,"
        }
        layerString.replace("null,", "")
        layerString = layerString.removeSuffix(",")
        return  layerString
    }
}