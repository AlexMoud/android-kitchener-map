package gr.hua.it21533.kitchenerMap.interfaces

import gr.hua.it21533.kitchenerMap.models.Features

interface MenuView {
    fun didFilterChange()
    fun showLoader()
    fun hideLoader()
    fun zoomOnPlace(features: Features)
    fun backToMenu()
    fun replaceMenuFragments(menuItem: String)
    fun setLocale(lang: String, reload: Boolean)
}