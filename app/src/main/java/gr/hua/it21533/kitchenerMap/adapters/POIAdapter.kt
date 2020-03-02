package gr.hua.it21533.kitchenerMap.adapters

import gr.hua.it21533.kitchenerMap.models.Features
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import gr.hua.it21533.kitchenerMap.KitchenerMap
import gr.hua.it21533.kitchenerMap.R

class POIAdapter(private val context: Context, var data: List<Features>, private val clickListener: (Features) -> Unit) : RecyclerView.Adapter<POIViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): POIViewHolder = POIViewHolder(LayoutInflater.from(context).inflate(R.layout.search_result_row, p0, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(p0: POIViewHolder, p1: Int) {
        p0.bind(data[p1], clickListener)
    }
}

class POIViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private var nameView = view.findViewById<TextView>(R.id.name)
    private var descriptionView = view.findViewById<TextView>(R.id.desc)

    fun bind(item: Features, clickListener: (Features) -> Unit) {
        val isEnglish = KitchenerMap.applicationContext().selectedLocale == "en"
        if (isEnglish) {
            nameView.text = item.properties?.values?.nameEN
            descriptionView.text = item.properties?.values?.categoryEN
        }else {
            nameView.text = item.properties?.values?.nameEL
            descriptionView.text = item.properties?.values?.categoryEL
        }
        itemView.setOnClickListener {
            clickListener(item)
        }
    }
}