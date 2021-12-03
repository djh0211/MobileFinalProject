package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class
UserMenuAdapter(options: FirebaseRecyclerOptions<UserMenuTitle>)
    : FirebaseRecyclerAdapter<UserMenuTitle, UserMenuAdapter.ViewHolder>(options) {
    interface OnItemClickListener {
        fun OnItemClick(holder: UserMenuAdapter.ViewHolder, view: View)
    }
    var itemClickListener:OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user_menu2, parent, false)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserMenuTitle) {
        holder.idView.text = model.title
    }
}