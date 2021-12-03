package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.heolle_beoltteok.databinding.TestrowBinding


class TestRecyclerViewAdapter(val items:ArrayList<TestInfo>) : RecyclerView.Adapter<TestRecyclerViewAdapter.ViewHolder>() {
    val testFragment = TestFragment()

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View, position: Int, hour:String, minute:String, sec:String)
    }
    var itemClickListener: OnItemClickListener? = null
    //var itemClickListener2:OnItemClickListener? = null



    inner class ViewHolder(val binding: TestrowBinding) : RecyclerView.ViewHolder(binding.root){

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TestrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val TestName = holder.binding.testName
        val TestTime = holder.binding.testTime
        //val TestImage = holder.binding.testImage




//        TestFragment().itemClickListener = object : TestFragment.OnItemClickListener {
//                override fun OnItemClick() {
//                    NextImage.visibility = View.VISIBLE
//                }
//            }




        val hour2 = items[position].hour
        val minute = items[position].minute
        val sec = items[position].sec

        TestName.text = items[position].testName
        TestTime.text = items[position].testTime

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