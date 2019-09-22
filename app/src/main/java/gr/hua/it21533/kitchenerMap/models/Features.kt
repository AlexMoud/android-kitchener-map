package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonObject

class Features(json: JsonObject) {

	val type: String = json.get("type").asString
	val id: String = json.get("id").asString
	val geometryName: String = json.get("geometry_name").asString
	val properties: Properties? = Properties(json.get("properties").asJsonObject.get("properties").asString)
	val geometry: Geometry = Geometry(json.getAsJsonObject("geometry"))
}