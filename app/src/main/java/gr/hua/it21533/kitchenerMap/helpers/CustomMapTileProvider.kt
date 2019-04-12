package gr.hua.it21533.kitchenerMap.helpers

import android.content.res.AssetManager
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class CustomMapTileProvider(private val mAssets: AssetManager) : TileProvider {

    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        val image = readTileImage(x, y, zoom)
        return if(image == null) null else Tile(
            TILE_WIDTH,
            TILE_HEIGHT, image)
    }

    private fun readTileImage(x: Int, y: Int, zoom: Int): ByteArray? {
        var inputStream: InputStream? = null
        var buffer: ByteArrayOutputStream? = null

        try {
            inputStream = mAssets.open(getTileFilename(x, y, zoom))
            buffer = ByteArrayOutputStream()

            var nRead: Int
            val data = ByteArray(BUFFER_SIZE)

            nRead = inputStream.read(data, 0,
                BUFFER_SIZE
            )
            while (nRead != -1) {
                buffer.write(data, 0, nRead)
                nRead = inputStream.read(data, 0,
                    BUFFER_SIZE
                )
            }
            buffer.flush()

            return buffer.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        } finally {
            inputStream?.let {
                it.close()
            }
            buffer?.let {
                it.close()
            }
        }
    }

    private fun getTileFilename(x: Int, y: Int, zoom: Int): String {
        return "tiles/$zoom/$x/$y.png"
    }

    companion object {
        private val TILE_WIDTH = 256
        private val TILE_HEIGHT = 256
        private val BUFFER_SIZE = 16 * 1024
    }
}