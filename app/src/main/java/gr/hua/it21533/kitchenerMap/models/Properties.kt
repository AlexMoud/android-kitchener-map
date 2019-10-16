package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonArray
import com.google.gson.JsonParser

class Properties(string: String?) {

	val values : PropertyName?

	init {
		val escapedString = string?.replace("\\", "")
		val jsonArray = JsonParser().parse(escapedString).asJsonArray
		values = PropertyName(jsonArray)
	}
}

class PropertyName(json: JsonArray) {

	var categoryEN: String? = null
	var categoryEL: String? = null
	var nameEN: String? = null
	var nameEL: String? = null
	var GIDEN: String? = null
	var GIDEL: String? = null
	var UUIDEN: String? = null
	var UUIDEL: String? = null
	var dbNameEN: String? = null
	var dbNameEL: String? = null

	init {
	    json.forEach {
			val key = it.asJsonObject.get("name").asJsonObject.get("en").asString
			val enString = it.asJsonObject.get("value").asJsonObject.get("en").asString
			val elString = it.asJsonObject.get("value").asJsonObject.get("el").asString
			when (key) {
				"GID" -> {
					GIDEN = enString
					GIDEL = elString
				}
				"UUID" -> {
					UUIDEL = elString
					UUIDEN = enString
				}
				"Database table name" -> {
					dbNameEL = elString
					dbNameEN = enString
				}
				"Category" -> {
					categoryEL = elString
					categoryEN = enString
				}
				"Map label" -> {
					nameEL = elString
					nameEN = enString
				}
				"district_1" -> {

				}
			}
		}
	}
}


