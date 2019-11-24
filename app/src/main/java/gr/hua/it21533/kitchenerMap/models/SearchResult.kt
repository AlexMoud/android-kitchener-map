package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonObject
import gr.hua.it21533.kitchenerMap.KitchenerMap


open class SearchResult(json: JsonObject) {

	val type : String = json.get("type").asString
	val features : List<Features>
	val numberReturned : Int = json.get("numberReturned").asInt
	var crs : Crs? = null

	init {
	    val array = json.getAsJsonArray("features")
		val features = ArrayList<Features>()
		array?.let {
			for (i in 0 until it.size()) {
				features.add(Features(it[i].asJsonObject))
			}
		}
		val isEnglish = KitchenerMap.applicationContext().selectedLocale == "en"
		features.sortBy {
			if (isEnglish) {
				it.properties?.values?.nameEN
			}else {
				it.properties?.values?.nameEL
			}
		}
		features.removeAll {
			it.geometry.point == null && it.geometry.points == null
		}
		this.features = features
		if (json.get("crs").isJsonObject) {
			crs = Crs(json.getAsJsonObject("crs"))
		}
	}
}