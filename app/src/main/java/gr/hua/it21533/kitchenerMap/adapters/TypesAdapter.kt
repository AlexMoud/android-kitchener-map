package gr.hua.it21533.kitchenerMap.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.models.TypesModel
import kotlinx.android.synthetic.main.item_type_of_places_checkbox.view.*

class TypesAdapter(private val typesCheckboxes : ArrayList<TypesModel>,
                   private val context: Context,
                   private val selectedCheckboxes: ArrayList<String>,
                   private val clickListener: (String) -> Unit): RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return typesCheckboxes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_type_of_places_checkbox,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.typeCheckbox.text = typesCheckboxes[position].displayValue
        holder.bind(typesCheckboxes[position].apiValue, clickListener)
        if(selectedCheckboxes.contains(typesCheckboxes[position].apiValue)) holder.typeCheckbox.isChecked = true
    }
}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val typeCheckbox:CheckBox = view.type_checkbox

    fun bind(apiValue: String, clickListener: (String) -> Unit) {
        typeCheckbox.setOnClickListener { clickListener(apiValue) }
    }
}