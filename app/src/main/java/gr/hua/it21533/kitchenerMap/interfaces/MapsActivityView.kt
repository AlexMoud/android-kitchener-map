package gr.hua.it21533.kitchenerMap.interfaces

import gr.hua.it21533.kitchenerMap.networking.ApiModel

interface MapsActivityView {
    fun displayMarkers(markers: Array<ApiModel.Results>?)
    fun showLoading()
    fun hideLoading()
}