package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MyItemRecyclerViewAdapter(options: FirebaseRecyclerOptions<TestTitle>)
    : FirebaseRecyclerAdapter<TestTitle, MyItemRecyclerViewAdapter.ViewHolder>(options) {
    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View)
    }
    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        init {
            idView.setOnClickListener {
                itemClickListener!!.OnItemClick(this,it)
            }
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: TestTitle) {
        holder.idView.text = model.testTitle
    }
}