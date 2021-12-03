package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import com.example.heolle_beoltteok.databinding.NavrowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class navAdapter (options: FirebaseRecyclerOptions<scheduleData>)
    : FirebaseRecyclerAdapter<scheduleData, navAdapter.ViewHolder>(options) {

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: NavrowBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {

            binding.button4.setOnClickListener {
                itemClickListener!!.OnItemClick(this, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = NavrowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: scheduleData) {
        holder.binding.apply {

            textView8.text = model.schedule


        }
    }
}