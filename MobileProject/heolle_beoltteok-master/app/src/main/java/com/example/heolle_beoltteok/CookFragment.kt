package com.example.heolle_beoltteok

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heolle_beoltteok.databinding.AddDialogBinding
import com.example.heolle_beoltteok.databinding.DeleteDialogBinding
import com.example.heolle_beoltteok.databinding.FragmentCookBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class CookFragment : Fragment() {
    var binding: FragmentCookBinding? = null
    var dialogBinding: AddDialogBinding? = null
    var deleteBinding: DeleteDialogBinding? = null
    var total = 0
    var started = false
    var CookInfo_ArrayList: ArrayList<CookInfo> = ArrayList()
    var flag = true
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var adapter: CookRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCookBinding.inflate(layoutInflater, container, false)
        dialogBinding = AddDialogBinding.inflate(layoutInflater, container, false)
        deleteBinding = DeleteDialogBinding.inflate(layoutInflater, container, false)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        binding!!.minute.text = "00"
        firebaseDatainit(binding!!.root)
        initRecyclerView(binding!!.recyclerView)
        init()
        addCooking(binding!!.root, context as Context, adapter, dialogBinding!!, binding!!)
        deleteCooking(context as Context)
        return binding!!.root
    }

    fun init() {
        total = binding!!.minute.text.toString().toInt() * 60 + binding!!.second.text.toString().toInt()
        adapter.itemClickListener = object : CookRecyclerViewAdapter.OnItemClickListener {

            override fun OnItemClick(holder: CookRecyclerViewAdapter.ViewHolder, view: View) {
                val time: String
                var cookTimeText = holder.binding.CookingTime.text.toString()
                Log.d("asefasef", cookTimeText.get(cookTimeText.length - 1) + "dfsfse")
                if (checkNumber(cookTimeText)) {
                    time = cookTimeText
                } else {
                    time = cookTimeText.substring(0, cookTimeText.length - 1)
                }
                binding!!.minute.text = String.format("%02d", time.toInt())
                total = binding!!.minute.text.toString()
                        .toInt() * 60 + binding!!.second.text.toString()
                        .toInt()
            }
        }

        binding!!.startBtn.setOnClickListener {
            if (flag == true) {
                start()
            }
        }

        binding!!.pasueBtn.setOnClickListener {
            pause()
        }

        binding!!.stopBtn.setOnClickListener {
            stop()
        }


    }


    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        adapter = CookRecyclerViewAdapter(CookInfo_ArrayList)
        recyclerView.adapter = adapter
    }

    // firestore에서 데이터를 읽어오는 함수
    fun firebaseDatainit(view: View) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            // collection(Cooking_Info) > documentation(요리 이름) > field(CookingImage,CookingName,CookingTime)
            firestore.collection("Cooking_Info")
                    // Cooking_Info에 해당하는 collection의 모든 documentation을 불러온 것임 그게 result로 들어감
                    .get()
                    // result로 불러온 값을 CookInfo object로 변형 그렇게하면 정의해둔 data class CookInfo와 같은 형태로 사용 가능
                    .addOnSuccessListener { result ->
                        for (doc in result) {
                            CookInfo_ArrayList.add(doc.toObject(CookInfo::class.java))
                        }
                        adapter.notifyDataSetChanged()
                        val addbtn = view.findViewById<Button>(R.id.addTimer2)
                        val deletebtn = view.findViewById<Button>(R.id.deleteTimer)
                        if (!deletebtn.isEnabled){
                            deletebtn.isEnabled = true
                        }
                        if (!addbtn.isEnabled){
                            addbtn.isEnabled = true
                        }
                    }.addOnFailureListener {
                        Log.d("fail", it.message.toString())
                    }
        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
        }
    }

    fun start() {
        if (binding!!.minute.text == "00" && binding!!.second.text == "00") {
            Toast.makeText(context, "요리를 선택해주세요!!!", Toast.LENGTH_SHORT).show()
            return;
        }
        started = true
        //sub thread
        thread(start = true) {
            while (true) {
                Thread.sleep(1000)
                if (!started) break
                total = total - 1
                activity!!.runOnUiThread {
                    binding!!.minute.text = String.format("%02d", (total / 60) % 60)
                    binding!!.second.text = String.format("%02d", total % 60)
                }

            }

        }


        flag = false

    }

    fun pause() {
        started = false
        flag = true

    }

    fun stop() {
        started = false
        total = 0
        binding!!.minute.text = "00"
        binding!!.second.text = "00"
        flag = true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            dialogBinding!!.addDialogCookingImage.setImageURI(filePath)
        }
    }

    fun deleteCooking(context: Context) {
        val deleteTimerBtn = binding!!.deleteTimer

        deleteTimerBtn.setOnClickListener {
            val mBuilder = AlertDialog.Builder(context)
                    .setView(deleteBinding?.root)
                    .setCancelable(false)
                    .setTitle("요리 삭제")
            val mAlertDialog = mBuilder.show()
            val okButton = deleteBinding?.deleteDialogdeleteButton
            val noButton = deleteBinding?.deleteDialogCancleButton
            val deleteName = deleteBinding?.deleteDialogCookingName
            val confirmBtn = deleteBinding?.deleteConfirmBtn
            val deleteImageView = deleteBinding?.deleteDialogCookingImage

            var temp : String = ""
                confirmBtn?.setOnClickListener {
                    if (deleteName?.text.toString() == ""){
                        Toast.makeText(context, "삭제할 요리의 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        for (i in 0 until CookInfo_ArrayList.size) {
                            if (CookInfo_ArrayList.get(i).cookingName.toLowerCase() == deleteName?.text.toString().toLowerCase()) {
                                GlobalScope.launch {
                                    val url : URL = URL(CookInfo_ArrayList.get(i).cookingImg)
                                    val conn : HttpURLConnection = url.openConnection() as HttpURLConnection
                                    conn.doInput = true
                                    conn.connect()
                                    val Is : InputStream = conn.inputStream
                                    val bitmap : Bitmap = BitmapFactory.decodeStream(Is)
                                    withContext(Dispatchers.Main) {
                                        deleteImageView?.setImageBitmap(bitmap)
                                        deleteBinding?.deleteDialogdeleteButton?.isEnabled = true
                                    }
                                }
                                temp = CookInfo_ArrayList.get(i).cookingName
                                break
                            }
                        }
                    }
                }
            okButton?.setOnClickListener {
                GlobalScope.launch {
                    deleteImage(temp)
                }
                deleteBinding!!.deleteDialogCookingImage.setImageResource(R.drawable.image)
                mAlertDialog.dismiss()
                (deleteBinding!!.root.parent as ViewGroup).removeView(deleteBinding!!.root)
            }
            noButton?.setOnClickListener {
                deleteBinding!!.deleteDialogCookingName.text.clear()
                deleteBinding!!.deleteDialogCookingImage.setImageResource(R.drawable.image)
                mAlertDialog.dismiss()
                (deleteBinding!!.root.parent as ViewGroup).removeView(deleteBinding!!.root)
            }
        }
    }

    fun addCooking(view: View, context: Context, adapter: CookRecyclerViewAdapter, dialogBinding: AddDialogBinding, cookBinding: FragmentCookBinding) {
        Log.d("saefasef", CookInfo_ArrayList.size.toString())
        val addTimerBtn = view.findViewById<Button>(R.id.addTimer2)

        addTimerBtn.setOnClickListener {
            val mBuilder = AlertDialog.Builder(context)
                    .setView(dialogBinding.root)
                    .setCancelable(false)
                    .setTitle("요리 추가")
            val mAlertDialog = mBuilder.show()
            val okButton = dialogBinding.addDialogAddButton
            val noButton = dialogBinding.addDialogCancleButton
            val addImageLayout = dialogBinding.addImageLayout
            addImageLayout.setOnClickListener {
                launchGallery()
            }

            okButton.setOnClickListener {
                GlobalScope.launch {
                        uploadImage(dialogBinding, adapter, cookBinding)
                }
                mAlertDialog.dismiss()
                (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)
            }
            noButton.setOnClickListener {
                dialogBinding!!.addDialogCookingName.text.clear()
                dialogBinding!!.addDialogCookingTime.text.clear()
                dialogBinding!!.addDialogCookingImage.setImageResource(R.drawable.image)
                mAlertDialog.dismiss()
                (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)
            }
        }

    }

    // 갤러리 오픈
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // 받아온 갤러리 사진 fireStorage에 올리고 access token 받아와서 firestore에 올림
    private fun uploadImage(dialogBinding: AddDialogBinding, adapter: CookRecyclerViewAdapter, cookBinding: FragmentCookBinding) {
        val db = FirebaseFirestore.getInstance()
        var CookingName: String = ""
        var CookingImage: String = ""
        var CookingTime: String = ""

        if (filePath != null) {
            val na = UUID.randomUUID().toString()
            val ref = storageReference?.child("uploads/" + na)
            val uploadTask = ref?.putFile(filePath!!)?.addOnSuccessListener {

                storageReference?.child("uploads/" + na)?.downloadUrl?.addOnSuccessListener {
                    Log.d("cookingName", dialogBinding.addDialogCookingName.text.toString())
                    Log.d("cookingTime", dialogBinding.addDialogCookingTime.text.toString())
                    Log.d("cookingImage", it.toString())
                    CookingName = dialogBinding.addDialogCookingName.text.toString()
                    CookingTime = dialogBinding.addDialogCookingTime.text.toString()
                    CookingImage = it.toString()

                    var newCook = hashMapOf(
                            "cookingImg" to CookingImage,
                            "cookingName" to CookingName,
                            "cookingTime" to CookingTime
                    )
                    db.collection("Cooking_Info")
                            .document(CookingName)
                            .set(newCook)
                            .addOnSuccessListener {
                                CookInfo_ArrayList.add(CookInfo(CookingName, CookingTime, CookingImage))
                                dialogBinding.addDialogCookingName.text.clear()
                                dialogBinding.addDialogCookingTime.text.clear()
                                dialogBinding!!.addDialogCookingImage.setImageResource(R.drawable.image)
                                adapter.notifyDataSetChanged()
                            }
                }?.addOnFailureListener {
                    Log.d("acees token", "acees token get fail")
                }
            }
        }
    }

    private fun deleteImage(CookingName : String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Cooking_Info").document(CookingName)
                .delete().addOnSuccessListener {
                    Toast.makeText(context, "요리를 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                    for (i in 0 until CookInfo_ArrayList.size){
                        if (CookInfo_ArrayList.get(i).cookingName == CookingName){
                            CookInfo_ArrayList.removeAt(i)
                            adapter.notifyDataSetChanged()
                            deleteBinding!!.deleteDialogCookingName.text.clear()
                            break
                        }
                    }
                }

    }
    fun checkNumber(str: String): Boolean {
        var check: Char

        if (str.equals("")) {
            return false
        }

        for (i in 0 until str.length) {
            check = str.get(i)
            if (check.toInt() < 48 || check.toInt() > 58) {
                return false
            }
        }
        return true
    }
}


