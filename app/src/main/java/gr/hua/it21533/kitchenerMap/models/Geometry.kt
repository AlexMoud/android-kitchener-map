package gr.hua.it21533.kitchenerMap.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject


class Geometry(json: JsonObject) {

	private val isPoint: Boolean = json.get("type").asString == "Point"
	private val isMultiLineString: Boolean = json.get("type").asString == "MultiLineString"
	private val isMultiPoligon: Boolean = json.get("type").asString == "MultiPolygon"
	var point: LatLng? = null
	var points: List<LatLng>? = null

	init {
	    when {
			isPoint -> {
				val lng = json.getAsJsonArray("coordinates").get(0).asDouble
				val lat = json.getAsJsonArray("coordinates").get(1).asDouble
				if (lat != null && lng != null) {
					point = LatLng(lat, lng)
				}
			}
			isMultiLineString -> {
				val coordinatesArray = json.getAsJsonArray("coordinates")
				if (coordinatesArray != null && coordinatesArray.size() > 0) {
					val points = ArrayList<LatLng>()
					val realArray = coordinatesArray.get(0).asJsonArray
					for (i in 0 until realArray.size()) {
						val lng = realArray.get(i).asJsonArray.get(0).asDouble
						val lat = realArray.get(i).asJsonArray.get(1).asDouble
						if (lat != null && lng != null) {
							points.add(LatLng(lat, lng))
						}
					}
					this.points = points
				}
			}
			isMultiPoligon -> {
				val coordinatesArray = json.getAsJsonArray("coordinates").get(0).asJsonArray
				if (coordinatesArray != null && coordinatesArray.size() > 0) {
					val points = ArrayList<LatLng>()
					val realArray = coordinatesArray.get(0).asJsonArray
					for (i in 0 until realArray.size()) {
						val lng = realArray.get(i).asJsonArray.get(0).asDouble
						val lat = realArray.get(i).asJsonArray.get(1).asDouble
						if (lat != null && lng != null) {
							points.add(LatLng(lat, lng))
						}
					}
					this.points = points
				}
			}
		}
	}
}