package gr.hua.it21533.kitchenerMap.models

import com.google.gson.annotations.SerializedName


data class Base (

	val id : Int,
	val name : Name,
	val groupIsOn : Boolean,
	val groupIsClosed : Boolean,
	val userOrder : Int,
	val opacity : Int,
	val type : String,
	val layers : List<Layers>
)