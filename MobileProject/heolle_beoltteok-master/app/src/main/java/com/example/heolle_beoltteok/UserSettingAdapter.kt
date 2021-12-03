package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserSettingAdapter(
    val items:ArrayList<UserItemInfo>
) : RecyclerView.Adapter<UserSettingAdapter.ViewHolder>() {
//        interface OnItemClickListener {
//            fun OnItemClick(holder: SettingAdapter.ViewHolder, view: View)
//        }
//        var itemClickListener:OnItemClickListener? = null

    fun moveItem(oldPos: Int, newPos: Int):List<Int> {
        val list = listOf<Int>(oldPos,newPos)
        val item = items[oldPos]
        items.removeAt(oldPos)
        items.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
        return list
    }

    fun removeItem(pos: Int):Int {
        items.removeAt(pos)
        notifyItemRemoved(pos)
        return pos
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.settingrow2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemName.text = item.itemName
        holder.itemTime.text = item.itemTime
        holder.hour.text = item.hour
        holder.min.text = item.minute
        holder.sec.text = item.sec

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName = view.findViewById<TextView>(R.id.itemName2)
        val itemTime = view.findViewById<TextView>(R.id.itemTime2)
        val hour = view.findViewById<TextView>(R.id.hour)
        val min = view.findViewById<TextView>(R.id.minute)
        val sec = view.findViewById<TextView>(R.id.sec)


    }
}
