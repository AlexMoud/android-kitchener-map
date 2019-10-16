package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonObject

class Features(json: JsonObject) {

	val type: String = json.get("type").asString
	val id: String = json.get("id").asString
	val geometryName: String = json.get("geometry_name").asString
	var properties: Properties? = null
	val geometry: Geometry = Geometry(json.getAsJsonObject("geometry"))
	var poiProperties: POIProperties? = null

	init {
		if (json.has("properties") && json.get("properties").isJsonObject && json.getAsJsonObject("properties").has("properties")) {
			properties = Properties(json.get("properties").asJsonObject.get("properties").asString)
		}
		if (json.has("properties") && json.get("properties").isJsonObject && json.getAsJsonObject("properties").has("display_properties")) {
			properties = Properties(json.get("properties").asJsonObject.get("display_properties").asString)
		}
		if (json.has("properties") && json.get("properties").isJsonObject) {
			poiProperties = POIProperties(json.getAsJsonObject("properties"))
		}
	}
}

class POIProperties(json: JsonObject) {
	var district: String? = null
	var name: String? = null
	var secondName: String? = null
	var nameGreek: String? = null
	var nameRoman: String? = null

	init {
		if (json.has("district_1") && !json.get("district_1").isJsonNull) {
			district = json.get("district_1").asString
		}
        if (json.has("name2") && !json.get("name2").isJsonNull ) {
			secondName = json.get("name2").asString
		}
		if (json.has("greek") && !json.get("greek").isJsonNull) {
			nameGreek = json.get("greek").asString
		}
		if (json.has("roman") && !json.get("roman").isJsonNull) {
			nameRoman = json.get("roman").asString
		}
	}
}