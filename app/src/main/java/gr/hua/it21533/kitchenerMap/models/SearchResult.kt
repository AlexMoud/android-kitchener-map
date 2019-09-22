package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonObject
import gr.hua.it21533.kitchenerMap.KitchenerMap


class SearchResult(json: JsonObject) {

	val type : String = json.get("type").asString
	val features : List<Features>
	val totalFeatures : Int = json.get("totalFeatures").asInt
	val numberMatched : Int = json.get("numberMatched").asInt
	val numberReturned : Int = json.get("numberReturned").asInt
	val timeStamp : String = json.get("timeStamp").asString
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
		this.features = features
		if (json.get("crs").isJsonObject) {
			crs = Crs(json.getAsJsonObject("crs"))
		}
	}
}