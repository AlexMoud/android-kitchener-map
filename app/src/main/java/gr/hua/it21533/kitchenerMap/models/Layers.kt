package gr.hua.it21533.kitchenerMap.models

data class Layers (

	val type : String,
	val id : String,
	val nameID : String,
	val name : Name,
	val description : Description,
	val userOrder : Int,
	val isOn : Boolean,
	val opacity : Int,
	val src : String
)