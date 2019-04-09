package gr.hua.it21533.kitchenerMap

// represents the google maps api response object
object ApiModel {
    data class Result(val results: Array<Results>)
    data class Results(val geometry: Geometry, val name: String?, val vicinity: String?)
    data class Geometry(val location: Location)
    data class Location(val lat: Double, val lng: Double)
}
