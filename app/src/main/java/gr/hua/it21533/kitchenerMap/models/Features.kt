package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonObject

class Features(json: JsonObject) {

	val type: String = json.get("type").asString
	val id: String = json.get("id").asString
	val geometryName: String = json.get("geometry_name").asString
	var properties: Properties? = null
	val geometry: Geometry = Geometry(json.getAsJsonObject("geometry"))

	init {
		if (json.has("properties") && json.get("properties").isJsonObject && json.getAsJsonObject("properties").has("properties")) {
			properties = Properties(json.get("properties").asJsonObject.get("properties").asString)
		}
		if (json.has("properties") && json.get("properties").isJsonObject && json.getAsJsonObject("properties").has("display_properties")) {
			properties = Properties(json.get("properties").asJsonObject.get("display_properties").asString)
		}
	}
}