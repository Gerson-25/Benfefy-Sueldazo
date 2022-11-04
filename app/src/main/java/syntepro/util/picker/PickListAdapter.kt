package syntepro.util.picker

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.syntepro.sueldazo.R

class PickListAdapter<T: ListablePicker>(private val pickList: List<T>, private val listener: OnItemPickListClickListener<T>) : androidx.recyclerview.widget.RecyclerView.Adapter<PickListAdapter<T>.PickListViewHolder>() {

    private var layoutType =  PickerDialog.ONE_ROW_LAYOUT
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickListViewHolder {
        val v: View = if(layoutType == PickerDialog.ONE_ROW_LAYOUT)
            LayoutInflater.from(parent.context).inflate(R.layout.picklist_onerow_layout, parent, false)
        else
            LayoutInflater.from(parent.context).inflate(R.layout.picklist_layout, parent, false)
        return PickListViewHolder(v)
    }

    override fun onBindViewHolder(holder: PickListViewHolder, position: Int) {
        val pickItem = pickList[position]
        if(pickItem.getCodeValue().isEmpty())
            holder.codeId.visibility = View.GONE
        else
            holder.codeId.visibility = View.VISIBLE
        holder.codeId.text = pickItem.getCodeValue()
        holder.nameId.text = pickItem.getTitleValue()
        holder.descId.text = pickItem.getDescValue()
        if(TextUtils.isEmpty(pickItem.getTitleValue())) holder.nameId.visibility = View.GONE else holder.nameId.visibility = View.VISIBLE
        if(TextUtils.isEmpty(pickItem.getDescValue())) holder.descId.visibility = View.GONE else holder.descId.visibility = View.VISIBLE
        holder.rootView.setOnClickListener { listener.onItemClicked(pickItem, position) }
    }

    override fun getItemCount(): Int {
        return pickList.size
    }

    fun setLayoutType(layoutType: Int) {
        this.layoutType = layoutType
    }

    inner class PickListViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val codeId: TextView = itemView.findViewById(R.id.pickerCodId)
        val nameId: TextView = itemView.findViewById(R.id.pickerNameId)
        val descId: TextView = itemView.findViewById(R.id.pickeDescId)
        val rootView: LinearLayout = itemView.findViewById(R.id.rootView)

    }
}