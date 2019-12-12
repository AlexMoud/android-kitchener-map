package gr.hua.it21533.kitchenerMap.models

data class HuaSettings(
    val baseMapGroups: List<BaseMapGroup>,
    val introJS: IntroJS,
    val layerGroups: List<LayerGroup>,
    val settings: Settings
)

data class BaseMapGroup(
    val alwaysOn: Boolean,
    val description: Description,
    val groupIsClosed: Boolean,
    val groupIsOn: Boolean,
    val hasTurnOffAllLayersButton: Boolean,
    val id: Int,
    val layers: List<LayerX>,
    val name: Name,
    val opacity: Int,
    val userOrder: Int
)

data class Description(
    val el: String,
    val en: String
)

data class Layer(
    val attribution: Attribution,
    val bounds: List<Double>,
    val description: Description,
    val geometryType: String,
    val iconLegend: String,
    val id: Int,
    val isOn: Boolean,
    val name: Name,
    val opacity: Double,
    val properties: List<Any>,
    val showRecords: Boolean,
    val src: String,
    val tileSize: Int,
    val type: String,
    val userOrder: Int
)

data class LayerX(
    val bounds: List<Double>?,
    val description: Description,
    val id: String,
    val isOn: Boolean,
    val name: Name,
    val opacity: Double,
    val parent: String?,
    val src: String,
    val type: String,
    val userOrder: Int
)

data class Attribution(
    val el: String,
    val en: String
)

data class Name(
    val el: String,
    val en: String
)

data class IntroJS(
    val intro1: Intro,
    val intro10: Intro,
    val intro11: Intro,
    val intro12: Intro,
    val intro13: Intro,
    val intro14: Intro,
    val intro15: Intro,
    val intro16: Intro,
    val intro17: Intro,
    val intro18: Intro,
    val intro19: Intro,
    val intro2: Intro,
    val intro3: Intro,
    val intro4: Intro,
    val intro5: Intro,
    val intro6: Intro,
    val intro7: Intro,
    val intro8: Intro,
    val intro9: Intro
)

data class Intro(
    val el: String,
    val en: String
)

data class LayerGroup(
    val basemapID: Int,
    val description: Description,
    val groupIsClosed: Boolean,
    val groupIsOn: Boolean,
    val hasTurnOffAllLayersButton: Boolean,
    val hide: Boolean,
    val id: Int,
    val layers: List<LayerX>,
    val name: Name,
    val opacity: Int,
    val sortLayers: String,
    val type: String,
    val userOrder: Int
)

data class Settings(
    val defaultZoom: Int,
    val graticuleInterval: Double,
    val graticuleStyle: GraticuleStyle,
    val historicMaps: HistoricMaps,
    val host: String,
    val initialExtent: List<Double>,
    val initialMapCenter: List<Int>,
    val initialMapZoom: Double,
    val lang: String,
    val languages: List<String>,
    val mapExtents: MapExtents,
    val maxNumberOfSearchResults: Int,
    val maxZoom: Int,
    val minLengthOfSearchText: Int,
    val minZoom: Int,
    val overviewMapErrorTileSrc: String,
    val overviewMapHeight: Int,
    val overviewMapSrc: String,
    val overviewMapWidth: Int,
    val path: String,
    val protocol: String,
    val showAnnotationForVisibleLayers: Boolean
)

data class GraticuleStyle(
    val fill_: Any,
    val geometry_: Any,
    val image_: Any,
    val renderer_: Any,
    val stroke_: Stroke,
    val text_: Any
)

data class Stroke(
    val color_: String,
    val lineDash_: List<Int>,
    val width_: Int
)

data class HistoricMaps(
    val cyprus: Cyprus,
    val lemessos: Lemessos,
    val nicosia: Nicosia
)

data class Cyprus(
    val layerID: Int
)

data class Lemessos(
    val layerID: Int
)

data class Nicosia(
    val layerID: Int
)

data class MapExtents(
    val cyprus: List<Double>,
    val lemessos: List<Double>,
    val nicosia: List<Double>
)