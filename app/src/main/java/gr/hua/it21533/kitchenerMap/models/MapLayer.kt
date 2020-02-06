package gr.hua.it21533.kitchenerMap.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class MapLayer : Parcelable {

    var name: String? = null
        private set
    var isFavorite: Boolean = false
    lateinit var data: LayerX
    var order: Int = 0

    constructor(name: String, isFavorite: Boolean, data: LayerX, order: Int) {
        this.name = name
        this.isFavorite = isFavorite
        this.data = data
        this.order = order + data.userOrder
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is MapLayer) return false

        val artist = o as MapLayer?

        if (isFavorite != artist!!.isFavorite) return false
        return if (name != null) name == artist.name else artist.name == null

    }

    override fun hashCode(): Int {
        var result = if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (isFavorite) 1 else 0
        return result
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<MapLayer> = object : Parcelable.Creator<MapLayer> {
            override fun createFromParcel(`in`: Parcel): MapLayer {
                return MapLayer(`in`)
            }

            override fun newArray(size: Int): Array<MapLayer?> {
                return arrayOfNulls(size)
            }
        }
    }
}