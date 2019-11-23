package gr.hua.it21533.kitchenerMap.models

import android.os.Build
import android.text.Html


data class Gravoura(
    val features: List<Feature>,
    val type: String
)

data class Feature(
    val geometry: Geometry,
    val properties: PropertiesX,
    val type: String
) {
    data class Geometry(
        val coordinates: List<Double>,
        val type: String
    )
}

data class PropertiesX(
    val clearName: String,
    val link: String,
    val name: String,
    val number: String,
    val orientation: Int,
    val thumbnail: String,
    val type: String
) {
    fun getNameCrpped(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(name, Html.FROM_HTML_MODE_COMPACT).toString()
        } else {
            Html.fromHtml(name).toString()
        }

    }
}