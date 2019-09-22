package gr.hua.it21533.kitchenerMap.models

import com.google.gson.JsonObject


class Crs(json: JsonObject) {

	val type : String = json.get("type").asString

}

//"crs":{
//	"type":"name",
//	"properties":{
//		"name":"urn:ogc:def:crs:EPSG::3857"
//	}
//}