package gr.hua.it21533.kitchenerMap.interfaces

import com.google.android.gms.maps.model.LatLng
import gr.hua.it21533.kitchenerMap.models.Features
import gr.hua.it21533.kitchenerMap.models.LayerX
import gr.hua.it21533.kitchenerMap.models.MapLayer

interface MenuView {
    fun didSelectMapOverlay(layer: LayerX, position: Int)
    fun didFilterChange()
    fun showLoader()
    fun hideLoader()
    fun zoomOnPlace(features: Features, location: LatLng?)
    fun backToMenu()
    fun replaceMenuFragments(menuItem: String)
    fun setLocale(lang: String, reload: Boolean)
    fun gravouraSelected()
    fun gravouraDeselected()
}