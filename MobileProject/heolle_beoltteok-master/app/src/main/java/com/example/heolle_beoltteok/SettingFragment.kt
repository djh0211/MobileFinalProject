package com.example.heolle_beoltteok

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heolle_beoltteok.databinding.FragmentSettingBinding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class SettingFragment : Fragment() {
    val firestore = FirebaseFirestore.getInstance()
    var binding: FragmentSettingBinding? = null
    lateinit var testTitle:String
    lateinit var date:String
    lateinit var newTest:HashMap<String, String>
    var newTestArray: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()




    lateinit var rdb: DatabaseReference


    var TestInfo_ArrayList : ArrayList<TestInfo> = ArrayList()

    lateinit var dialogView:View
    lateinit var adapter: SettingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        dialogView = inflater.inflate(R.layout.add_dialog2, container, false)

        init()

        return binding!!.root


    }



    private fun init() {
        rdb = FirebaseDatabase.getInstance().getReference("datas/items")


        binding!!.recyclerView.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        adapter = SettingAdapter(TestInfo_ArrayList)
        binding!!.recyclerView.adapter = adapter


        binding!!.addBtn2.setOnClickListener {
            if(binding!!.addText.text.isEmpty() || binding!!.addText2.text.isEmpty()) {
                val builder = AlertDialog.Builder(context)
                        .setTitle("입력 오류")
                        .setMessage("시험이름, 시험날짜를 모두 입력하세요.")

                        .show()
                return@setOnClickListener
            }


            val mBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle("단어 추가")
                    .show()
            //val mAlertDialog = mBuilder.show()

            val okButton = dialogView.findViewById<Button>(R.id.addDialogAddButton)
            val noButton = dialogView.findViewById<Button>(R.id.addDialogCancleButton)
            val testName = dialogView.findViewById<EditText>(R.id.addDialogCookingName)
            val testTime= dialogView.findViewById<EditText>(R.id.addDialogCookingTime)
            okButton.setOnClickListener {

                val test = TestInfo(testName.text.toString(), testTime.text.toString(), "", String.format("%02d", testTime.text.toString().toInt() / 60), String.format("%02d",  testTime.text.toString().toInt() % 60), "00")
                TestInfo_ArrayList.add(test)
                adapter.notifyDataSetChanged()

                newTest = hashMapOf<String, String>(
                        "testName" to testName.text.toString(),
                        "testTime" to testTime.text.toString(),
                        "hour" to String.format("%02d",  testTime.text.toString().toInt() / 60),
                        "minute" to String.format("%02d",  testTime.text.toString().toInt()% 60),
                        "sec" to "00"
                )
                newTestArray.add(newTest)


                testTitle = binding!!.addText.text.toString()
                date = binding!!.addText2.text.toString()

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
            if(newTestArray.isEmpty()) {
                val builder = AlertDialog.Builder(context)
                        .setTitle("입력 오류")
                        .setMessage("내용을 입력하세요.")

                        .show()
                return@setOnClickListener
            }


            rdb.child(testTitle).setValue(TestTitle(testTitle, date))
            var num = 0
            for(i in newTestArray) {

                var testName = i.get("testName")
                if (testName != null) {
                    firestore.collection(testTitle).document("$num"+"."+testName).set(i)
                    num++
                }
            }
            val fragment = activity!!.supportFragmentManager.beginTransaction()
            //fragment.addToBackStack(null)
            fragment.replace(R.id.frameLayout, ItemFragment2())
            fragment.commit()
        }

        binding!!.cancelBtn.setOnClickListener {
            val fragment = activity!!.supportFragmentManager.beginTransaction()
            //fragment.addToBackStack(null)
            fragment.replace(R.id.frameLayout, ItemFragment2())
            fragment.commit()
        }
        val simpleCallBack = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP,
                ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
            ): Boolean {

                val pos = adapter.moveItem(viewHolder.adapterPosition,target.adapterPosition)
                val temp = newTestArray.get(pos.get(0))
                val temp2 = newTestArray.get(pos.get(1))

                newTestArray.set(pos.get(0),temp2)
                newTestArray.set(pos.get(1),temp)
                Log.i("pos",newTestArray.get(1).toString())
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = adapter.removeItem(viewHolder.adapterPosition)
                Log.e("position",pos.toString())
                newTestArray.removeAt(pos)

            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(binding!!.recyclerView)
    }


    }


