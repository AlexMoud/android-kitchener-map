package gr.hua.it21533.kitchenerMap.helpers

import com.google.android.gms.maps.model.TileProvider
import java.net.MalformedURLException
import java.net.URL


object TileProviderFactory {

    private const val GEOSERVER_FORMAT = "https://gaia.hua.gr/geoserver/ows?service=WMS&resource=02422ff9-9e60-430f-bbc5-bb5324359198" +
                                            "&version=1.3.0" +
                                            "&request=GetMap" +
                                            "&layers=%s" +
                                            "&bbox=%f,%e,%d,%g" +
                                            "&width=256" +
                                            "&height=256" +
                                            "&srs=EPSG:3857" +
                                            "&format=image/png" +
                                            "&transparent=true"

    private const val LEGEND_FORMAT = "https://gaia.hua.gr/geoserver/ows?service=WMS&resource=02422ff9-9e60-430f-bbc5-bb5324359198" +
                                        "&version=1.3.0" +
                                        "&request=GetLegendGraphic" +
                                        "&layers=%s" +
                                        "&bbox=%f,%e,%d,%g" +
                                        "&width=256" +
                                        "&height=256" +
                                        "&srs=EPSG:3857" +
                                        "&format=image/png" +
                                        "&transparent=true"

    var layers: ArrayList<String> = ArrayList()

    // return a geoserver wms tile layer
    val tileProvider: TileProvider get() = object: WMSTileProvider(256, 256) {

        @Synchronized
        override fun getTileUrl(x:Int, y:Int, zoom:Int): URL? {
            val bbox = getBoundingBox(x, y, zoom)

            var layerString = ""
            layers.forEach {
                layerString = "$layerString$it,"
            }
            layerString = layerString.removeSuffix(",")
            val s = GEOSERVER_FORMAT.replace("%s", layerString).replace("%f", bbox[MINX].toString()).replace("%e", bbox[MINY].toString()).replace("%d", bbox[MAXX].toString()).replace("%g", bbox[MAXY].toString())
            var url:URL? = null
            try {
                url = URL(s)
            } catch (e: MalformedURLException) {
                throw AssertionError(e)
            }

            return url
        }
    }

    val tileProviderLegend: TileProvider get() = object: WMSTileProvider(256, 256) {

        @Synchronized
        override fun getTileUrl(x:Int, y:Int, zoom:Int): URL? {
            val bbox = getBoundingBox(x, y, zoom)

            var layerString = ""
            layers.forEach {
                layerString = "$layerString$it,"
            }
            layerString = layerString.removeSuffix(",")
            val s = LEGEND_FORMAT.replace("%s", layerString).replace("%f", bbox[MINX].toString()).replace("%e", bbox[MINY].toString()).replace("%d", bbox[MAXX].toString()).replace("%g", bbox[MAXY].toString())
            var url:URL? = null
            try {
                url = URL(s)
            } catch (e: MalformedURLException) {
                throw AssertionError(e)
            }

            return url
        }
    }
}