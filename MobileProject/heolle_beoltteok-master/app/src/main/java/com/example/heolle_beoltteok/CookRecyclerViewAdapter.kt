package com.example.heolle_beoltteok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heolle_beoltteok.databinding.CookingrowBinding

class CookRecyclerViewAdapter(val items:ArrayList<CookInfo>) : RecyclerView.Adapter<CookRecyclerViewAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: CookingrowBinding, val view: View) : RecyclerView.ViewHolder(binding.root){
        // 아이템 하나를 클릭했을 때 그 아이템에 해당하는 조리 시간이 타이머로 넘어가게 하는 함수
        init {

            binding.CookingImage.setOnClickListener {
                itemClickListener!!.OnItemClick(this, it)
            }
        }

        fun data_bind(holder: ViewHolder, position: Int) {
            val CookName = holder.binding.CookingName
            val CookTime = holder.binding.CookingTime
            val CookImage = holder.binding.CookingImage

            CookName.text = items[position].cookingName
            CookTime.text = items[position].cookingTime
            Glide.with(itemView).load(items[position].cookingImg).into(CookImage)
        }
        fun bind(holder: ViewHolder, position:Int) {
            data_bind(holder,position)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CookingrowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}