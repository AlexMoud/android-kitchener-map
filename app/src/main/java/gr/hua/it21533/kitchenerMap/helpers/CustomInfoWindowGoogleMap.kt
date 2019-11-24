package gr.hua.it21533.kitchenerMap.helpers

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.models.GravouraInfoWindowData
import kotlinx.android.synthetic.main.map_custom_infowindow.view.*


class CustomInfoWindowGoogleMap(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = (context as Activity).layoutInflater.inflate(R.layout.map_custom_infowindow, null)


        val infoWindowData = marker.tag as GravouraInfoWindowData?

        view.name.text = infoWindowData?.name
        view.snipet.text = infoWindowData?.snipet

        Picasso.get().load(infoWindowData?.image).into(view.pic)
        if (infoWindowData?.image == null) {
            view.pic.visibility = View.GONE
        } else {
            view.snipet.visibility = View.GONE
        }

        return view
    }
}