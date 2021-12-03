package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.heolle_beoltteok.databinding.UsertimerrowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class UserTimerAdapter(val items:ArrayList<UserItemInfo>) : RecyclerView.Adapter<UserTimerAdapter.ViewHolder>() {
    val userTimerFragment = UserTimerFragment()

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View,position: Int,hour:String,minute:String,sec:String)
    }
    var itemClickListener:OnItemClickListener? = null
    //var itemClickListener2:OnItemClickListener? = null



    inner class ViewHolder(val binding: UsertimerrowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = UsertimerrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val ItemName = holder.binding.itemName
        val ItemTime = holder.binding.itemTime
        //val TestImage = holder.binding.testImage




//        TestFragment().itemClickListener = object : TestFragment.OnItemClickListener {
//                override fun OnItemClick() {
//                    NextImage.visibility = View.VISIBLE
//                }
//            }




        val hour2 = items[position].hour
        val minute = items[position].minute
        val sec = items[position].sec

        ItemName.text = items[position].itemName
        ItemTime.text = items[position].itemTime

        // Glide.with(holder.binding.root.context).load(items[position].testImg).into(TestImage)

        holder.binding.root.setOnClickListener {
            itemClickListener!!.OnItemClick(holder, it,position, hour2,minute,sec)
        }
//        holder.binding.nextButton.setOnClickListener {
//            itemClickListener2!!.OnItemClick(holder, it,position, hour2,minute,sec)
//        }


    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return position
    }


}