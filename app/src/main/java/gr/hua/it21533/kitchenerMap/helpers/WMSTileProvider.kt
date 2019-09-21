package gr.hua.it21533.kitchenerMap.helpers

import com.google.android.gms.maps.model.UrlTileProvider
import kotlin.math.pow

// Construct with tile size in pixels, normally 256, see parent class.
abstract class WMSTileProvider(x: Int, y: Int) : UrlTileProvider(x, y) {


    // Return a web Mercator bounding box given tile x/y indexes and a zoom
    // level.
    protected fun getBoundingBox(x: Int, y: Int, zoom: Int): DoubleArray {
        val tileSize = MAP_SIZE / 2.0.pow(zoom.toDouble())
        val minx = TILE_ORIGIN[ORIG_X] + x * tileSize
        val maxx = TILE_ORIGIN[ORIG_X] + (x + 1) * tileSize
        val miny = TILE_ORIGIN[ORIG_Y] - (y + 1) * tileSize
        val maxy = TILE_ORIGIN[ORIG_Y] - y * tileSize

        val bbox = DoubleArray(4)
        bbox[MINX] = minx
        bbox[MINY] = miny
        bbox[MAXX] = maxx
        bbox[MAXY] = maxy

        return bbox
    }

    companion object {

        // Web Mercator n/w corner of the map.
        private val TILE_ORIGIN = doubleArrayOf(-20037508.34789244, 20037508.34789244)
        //array indexes for that data
        private const val ORIG_X = 0
        private const val ORIG_Y = 1 // "

        // Size of square world map in meters, using WebMerc projection.
        private const val MAP_SIZE = 20037508.34789244 * 2

        // array indexes for array to hold bounding boxes.
        const val MINX = 0
        const val MAXX = 1
        const val MINY = 2
        const val MAXY = 3
    }
}