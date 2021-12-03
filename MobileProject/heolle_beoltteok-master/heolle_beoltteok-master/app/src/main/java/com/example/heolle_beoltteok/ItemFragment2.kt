package com.example.heolle_beoltteok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heolle_beoltteok.databinding.FragmentItem2Binding

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ItemFragment2 : Fragment() {
    val viewModel: MyViewModel by activityViewModels()

    lateinit var adapter: MyItemRecyclerViewAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var rdb: DatabaseReference


    var binding: FragmentItem2Binding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItem2Binding.inflate(layoutInflater, container, false)
        Datainit()
        init()
        return binding!!.root
    }
    fun Datainit() {
        rdb = FirebaseDatabase.getInstance().getReference("datas/items")

    }

    private fun init() {

        val query = rdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<TestTitle>()
            .setQuery(query, TestTitle::class.java)
            .build()

        adapter = MyItemRecyclerViewAdapter(option)


        layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding!!.recyclerview.layoutManager = layoutManager
        binding!!.recyclerview.adapter = adapter

        adapter.itemClickListener = object : MyItemRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(
                    holder: MyItemRecyclerViewAdapter.ViewHolder,
                    view: View,

                    ) {
                viewModel.liveData.value = holder.idView.text.toString()

                val fragment = activity!!.supportFragmentManager.beginTransaction()
                //fragment.addToBackStack(null)
                fragment.replace(R.id.frameLayout, IntroFragment())
                fragment.commit()
            }
        }
        binding!!.button2.setOnClickListener {
            val fragment = activity?.supportFragmentManager?.beginTransaction()
            //fragment.addToBackStack(null)
            fragment?.replace(R.id.frameLayout, SettingFragment())
            fragment?.commit()
        }

        adapter.startListening()


    }


}