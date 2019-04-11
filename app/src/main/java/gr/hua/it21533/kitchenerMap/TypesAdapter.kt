package gr.hua.it21533.kitchenerMap

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.type_checkbox_item.view.*

class TypesAdapter(val typesCheckboxes : ArrayList<TypesModel>,
                   val context: Context,
                   val clickListener: (String) -> Unit): RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of checkboxes in the list
    override fun getItemCount(): Int {
        return typesCheckboxes.size
    }

    // Inflates the checkboxes views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.type_checkbox_item, parent, false))
    }

    // Binds each checkbox in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // set displayed text to TypeModel.displayValue
        holder.typeCheckbox?.text = typesCheckboxes[position].displayValue

        // set clicklistener text to TypeModel.apiValue
        holder.bind(typesCheckboxes[position].apiValue, clickListener)
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val typeCheckbox = view.type_checkbox

    // bind each checkbox to a clicklistener
    fun bind(apiValue: String, clickListener: (String) -> Unit) {
        typeCheckbox.setOnClickListener { clickListener(apiValue) }
    }
}