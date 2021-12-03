package com.example.heolle_beoltteok

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heolle_beoltteok.databinding.FragmentUserMenuBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class UserMenuFragment : Fragment() {

    val viewModel: MyViewModel by activityViewModels()

    lateinit var adapter: UserMenuAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var rdb: DatabaseReference


    var binding: FragmentUserMenuBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserMenuBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        Datainit()
        init()
        return binding!!.root
    }

    fun Datainit() {
        rdb = FirebaseDatabase.getInstance().getReference("userFrag/items")

    }

    private fun init() {

        val query = rdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<UserMenuTitle>()
            .setQuery(query, UserMenuTitle::class.java)
            .build()

        adapter = UserMenuAdapter(option)


        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerview.layoutManager = layoutManager
        binding!!.recyclerview.adapter = adapter

        adapter.itemClickListener = object : UserMenuAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: UserMenuAdapter.ViewHolder,
                view: View,

                ) {

                val fragment = activity!!.supportFragmentManager.beginTransaction()
                //fragment.addToBackStack(null)
                fragment.replace(R.id.frameLayout, UserIntroFragment())
                fragment.commit()
                viewModel.liveData.value = holder.idView.text.toString()

            }
        }
        binding!!.button2.setOnClickListener {
            val fragment = activity?.supportFragmentManager?.beginTransaction()
            //fragment.addToBackStack(null)
            fragment?.replace(R.id.frameLayout, User_SettingFragment())
            fragment?.commit()
        }


        adapter.startListening()


    }

}