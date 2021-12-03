package com.example.heolle_beoltteok

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heolle_beoltteok.databinding.FragmentUserSettingBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import java.util.HashMap


class User_SettingFragment : Fragment() {
    var binding: FragmentUserSettingBinding? = null
    val firestore = FirebaseFirestore.getInstance()
    lateinit var rdb: DatabaseReference

    lateinit var itemTitle:String
    lateinit var newItem: HashMap<String, String>
    var newItemArray : ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()

    lateinit var dialogView:View
    lateinit var adapter: UserSettingAdapter

    var TestInfo_ArrayList : ArrayList<UserItemInfo> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserSettingBinding.inflate(layoutInflater, container, false)
        dialogView = inflater.inflate(R.layout.add_dialog4, container, false)

        init()

        return binding!!.root


    }



    private fun init() {
        rdb = FirebaseDatabase.getInstance().getReference("userFrag/items")


        binding!!.recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        adapter = UserSettingAdapter(TestInfo_ArrayList)
        binding!!.recyclerView.adapter = adapter


        binding!!.addBtn2.setOnClickListener {
            if(binding!!.addText.text.isEmpty() ) {
                val builder = AlertDialog.Builder(context)
                    .setTitle("입력 오류")
                    .setMessage("입력해주세요.")
                    .show()
                return@setOnClickListener
            }
            val mBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle("타이머 추가").show()
//            val mAlertDialog = mBuilder.show()

            val okButton = dialogView.findViewById<Button>(R.id.addDialogAddButton)
            val noButton = dialogView.findViewById<Button>(R.id.addDialogCancleButton)
            val testName = dialogView.findViewById<EditText>(R.id.addDialogCookingName)
            val testTime= dialogView.findViewById<EditText>(R.id.addDialogCookingTime)
            okButton.setOnClickListener {

                val test = UserItemInfo(testName.text.toString(),testTime.text.toString(),String.format("%02d", testTime.text.toString().toInt()/60),String.format("%02d", testTime.text.toString().toInt()%60),"00")
                TestInfo_ArrayList.add(test)
                adapter.notifyDataSetChanged()

                newItem = hashMapOf<String, String>(
                    "itemName" to testName.text.toString(),
                    "itemTime" to testTime.text.toString(),
                    "hour" to String.format("%02d",  testTime.text.toString().toInt() / 60),
                    "minute" to String.format("%02d",  testTime.text.toString().toInt()% 60),
                    "sec" to "00"
                )
                newItemArray.add(newItem)

                itemTitle = binding!!.addText.text.toString()

                testName.text.clear()
                testTime.text.clear()



            }
            noButton.setOnClickListener {
                if (dialogView.getParent() != null) {
                    (dialogView.getParent() as ViewGroup).removeView(dialogView)
                }
                testName.text.clear()
                testTime.text.clear()
                mBuilder.dismiss()
            }
        }

        binding!!.addBtn3.setOnClickListener {
            if(TestInfo_ArrayList.isEmpty()) {
                val builder = AlertDialog.Builder(context)
                        .setTitle("입력 오류")
                        .setMessage("내용을 입력하세요.")

                        .show()
                return@setOnClickListener
            }

            rdb.child(itemTitle).setValue(UserMenuTitle(itemTitle))
            var num = 0
            for(i in newItemArray) {

                var itemName = i.get("itemName")
                if (itemName != null) {
                    firestore.collection(itemTitle).document("$num"+"."+itemName).set(i)
                    num++
                }
            }
            val fragment = requireActivity().supportFragmentManager.beginTransaction()
            //fragment.addToBackStack(null)
            fragment.replace(R.id.frameLayout, UserMenuFragment())
            fragment.commit()
        }

        binding!!.cancelBtn.setOnClickListener {
            val fragment = requireActivity().supportFragmentManager.beginTransaction()
            //fragment.addToBackStack(null)
            fragment.replace(R.id.frameLayout, UserMenuFragment())
            fragment.commit()
        }
        val simpleCallBack = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
            ): Boolean {

                val pos = adapter.moveItem(viewHolder.adapterPosition,target.adapterPosition)
                val temp = newItemArray.get(pos.get(0))
                val temp2 = newItemArray.get(pos.get(1))

                newItemArray.set(pos.get(0),temp2)
                newItemArray.set(pos.get(1),temp)
                Log.i("pos",newItemArray.get(1).toString())
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = adapter.removeItem(viewHolder.adapterPosition)
                Log.e("position",pos.toString())
                newItemArray.removeAt(pos)

            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(binding!!.recyclerView)
    }


}