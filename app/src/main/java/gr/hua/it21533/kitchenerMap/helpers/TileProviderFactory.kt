package gr.hua.it21533.kitchenerMap.helpers

import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.models.LayerX


object TileProviderFactory {

    private const val GEOSERVER_FORMAT = "https://gaia.hua.gr/geoserver/ows?service=WMS" +
//            "&resource=02422ff9-9e60-430f-bbc5-bb5324359198" +
                                            "&version=1.3.0" +
                                            "&request=GetMap" +
                                            "&layers=%s" +
                                            "&bbox=%f,%e,%d,%g" +
                                            "&width=256" +
                                            "&height=256" +
                                            "&srs=EPSG:3857" +
                                            "&format=image/png" +
                                            "&transparent=true"

    var layers: ArrayList<LayerX> = ArrayList()

    // return a geoserver wms tile layer
    val tileProvider: WMSTileProvider get() = object: WMSTileProvider(256, 256, KitchenerMap.applicationContext()) {

        @Synchronized
        override fun getTileUrl(x:Int, y:Int, z:Int): String {
            val bbox = getBoundingBox(x, y, z)

            var layerString = ""
            val sortedLayers = layers.sortedBy { it.userOrder }.map { it.src }
            sortedLayers.forEach {
                layerString = "$layerString$it,"
            }
            layerString.replace("null,", "")
            layerString = layerString.removeSuffix(",")

            return GEOSERVER_FORMAT.replace("%s", layerString)
                .replace("%f", bbox[MINX].toString())
                .replace("%e", bbox[MINY].toString())
                .replace("%d", bbox[MAXX].toString())
                .replace("%g", bbox[MAXY].toString())
        }
    }
}
