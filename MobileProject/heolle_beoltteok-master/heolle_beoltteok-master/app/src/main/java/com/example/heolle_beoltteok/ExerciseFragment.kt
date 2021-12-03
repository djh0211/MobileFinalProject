package com.example.heolle_beoltteok

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heolle_beoltteok.databinding.FragmentExerciseBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.logging.Handler
import kotlin.concurrent.thread
import kotlin.concurrent.timer


class ExerciseFragment : Fragment() {
    var binding: FragmentExerciseBinding?=null
    lateinit var rdb: DatabaseReference
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: ExerciseAdapter
    var isFabOpen = false
    var findQuery = false
    var stat = 0
    var setCount = 0
    var time = 0
    var total = 0
    var total2 = 0
    var started = false
    var flag = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        val fabRClockwise = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise)
        val fabRAntiClockwise = AnimationUtils.loadAnimation(context, R.anim.rotate_anticlockwise)

        val exerciseView = inflater.inflate(R.layout.add_exercise_list, container, false)
        val deleteExerciseView = inflater.inflate(R.layout.delete_exercise_list, container, false)
        val setExerciseView = inflater.inflate(R.layout.add_exercise_set, container, false)
        binding = FragmentExerciseBinding.inflate(layoutInflater,container,false)
        binding!!.setNum.text = "$setCount"
        // Inflate the layout for this fragment
        binding?.fabEdit?.setOnClickListener {
            if(isFabOpen){
                binding?.fabAdd?.startAnimation(fabClose)
                binding?.fabDelete?.startAnimation(fabClose)
                binding?.fabEdit?.startAnimation(fabRClockwise)

                isFabOpen = false
            }
            else{
                binding?.fabAdd?.startAnimation(fabOpen)
                binding?.fabDelete?.startAnimation(fabOpen)
                binding?.fabEdit?.startAnimation(fabRAntiClockwise)

                binding?.fabAdd?.isClickable
                binding?.fabDelete?.isClickable

                isFabOpen = true
            }
        }
        binding?.fabAdd?.setOnClickListener {
            addExercise(exerciseView, context as Context)
        }
        binding?.fabDelete?.setOnClickListener {
            deleteExercise(deleteExerciseView, context as Context)
        }
        binding?.setText?.setOnClickListener {
            setExercise(setExerciseView, context as Context)
        }
        binding?.setNum?.setOnClickListener {
            setExercise(setExerciseView, context as Context)
        }

        initFB()
        init()
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rdb = FirebaseDatabase.getInstance().getReference("MyData/items")
//        initData()
    }
    fun setExercise(setExerciseView: View, context: Context){
        val mBuilder = AlertDialog.Builder(context)
            .setView(setExerciseView)
            .setCancelable(false)
            .setTitle("세트 수 변경")

        val mAlertDialog = mBuilder.show()
        val editBtn = setExerciseView.findViewById<Button>(R.id.EditExSetButton)
        val cancelBtn = setExerciseView.findViewById<Button>(R.id.CancelExsetButton)
        val setNum = setExerciseView.findViewById<EditText>(R.id.EditExSetNum)

        editBtn?.setOnClickListener {
            if(!chkNum(setNum.text.toString())){
                Toast.makeText(context, "잘못된 입력값 입니다 다시 입력하세요", Toast.LENGTH_SHORT).show()
                setNum.text.clear()
            }
            else{
                if(setNum.text.toString().length == 0){
                    Toast.makeText(context, "잘못된 입력값 입니다 다시 입력하세요", Toast.LENGTH_SHORT).show()
                    setNum.text.clear()
                }
                else {
                    setCount = setNum.text.toString().toInt()
                    binding!!.setNum.text = "$setCount"
                    setNum.text.clear()
                    mAlertDialog.dismiss()
                    (setExerciseView.parent as ViewGroup).removeView(setExerciseView)
                }
            }
        }
        cancelBtn?.setOnClickListener {
            setNum.text.clear()
            mAlertDialog.dismiss()
            (setExerciseView.parent as ViewGroup).removeView(setExerciseView)
        }
    }
    fun addExercise(exerciseView: View, context: Context) {
        rdb = FirebaseDatabase.getInstance().getReference("MyData/items")
        val mBuilder = AlertDialog.Builder(context)
            .setView(exerciseView)
            .setCancelable(false)
            .setTitle("운동 추가")

        val mAlertDialog = mBuilder.show()

        val okButton = exerciseView.findViewById<Button>(R.id.addExAddButton)
        val noButton = exerciseView.findViewById<Button>(R.id.addExCancelButton)
        val ExName = exerciseView.findViewById<EditText>(R.id.addExerciseName)
        val ExTime = exerciseView.findViewById<EditText>(R.id.addExerciseTime)
        val RelaxTime = exerciseView.findViewById<EditText>(R.id.addRelaxingTime)

        okButton?.setOnClickListener {
            if (ExName.text.toString().length == 0 ||
                ExTime.text.toString().length == 0 ||
                RelaxTime.text.toString().length == 0
            ) {
            } else {
                if (!chkNum(ExTime.text.toString()) && !chkNum(ExTime.text.toString())) {
                    Toast.makeText(context, "잘못된 입력값 입니다 다시 입력하세요", Toast.LENGTH_SHORT).show()

                    ExName.text.clear()
                    ExTime.text.clear()
                    RelaxTime.text.clear()
                } else {
                    rdb.child(ExName.text.toString()).setValue(
                        ExerciseData(
                            ExName.text.toString(),
                            (ExTime.text.toString().toInt() / 60).toString(),
                            (ExTime.text.toString().toInt() % 60).toString(),
                            (RelaxTime.text.toString().toInt() / 60).toString(),
                            (RelaxTime.text.toString().toInt() % 60).toString()
                        )
                    )

                    Toast.makeText(
                        context,
                        "${ExName.text.toString()}항목 추가",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    ExName.text.clear()
                    ExTime.text.clear()
                    RelaxTime.text.clear()
                    mAlertDialog.dismiss()
                    (exerciseView.parent as ViewGroup).removeView(exerciseView)
                }
            }
        }
        noButton?.setOnClickListener {
            ExName.text.clear()
            ExTime.text.clear()
            RelaxTime.text.clear()
            mAlertDialog.dismiss()
            (exerciseView.parent as ViewGroup).removeView(exerciseView)
        }
    }

    fun deleteExercise(deleteExerciseView: View, context: Context) {
        rdb = FirebaseDatabase.getInstance().getReference("MyData/items")

        val mBuilder = AlertDialog.Builder(context)
            .setView(deleteExerciseView)
            .setCancelable(false)
            .setTitle("운동 삭제")

        val mAlertDialog = mBuilder.show()
        val deleteButton = deleteExerciseView.findViewById<Button>(R.id.deleteExDeleteButton)
        val noButton = deleteExerciseView.findViewById<Button>(R.id.deleteExCancelButton)
        val ExName = deleteExerciseView.findViewById<EditText>(R.id.deleteExerciseName)

        deleteButton?.setOnClickListener {
            if (ExName.text.toString().length == 0) {

            } else {
                rdb.child(ExName.text.toString()).get().addOnSuccessListener {
                    if (it.value != null) {
                        rdb.child(it.key.toString()).removeValue()
                        Toast.makeText(context, "${it.key} 항목 delete", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "검색결과가 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
                ExName.text.clear()
                mAlertDialog.dismiss()
                (deleteExerciseView.parent as ViewGroup).removeView(deleteExerciseView)
            }
        }
        noButton?.setOnClickListener {
            ExName.text.clear()
            mAlertDialog.dismiss()
            (deleteExerciseView.parent as ViewGroup).removeView(deleteExerciseView)
        }
    }
    fun chkNum(str: String) : Boolean {
        var temp: Char
        var result = true
        for (i in 0 until str.length) {
            temp = str.elementAt(i)
            if (temp.toInt() < 48 || temp.toInt() > 57)
                result = false
        }
        return result
    }

//==========================================================================//


    private fun initData() {    //data push
        val scan = Scanner(resources.openRawResource(R.raw.data))
        while (scan.hasNextLine()){
            val e_name = scan.nextLine()
            val e_min = scan.nextLine()
            val e_sec = scan.nextLine()
            val r_min = scan.nextLine()
            val r_sec = scan.nextLine()

            val item = ExerciseData(e_name, e_min, e_sec, r_min, r_sec)
            rdb.child(e_name).setValue(item)

        }
        scan.close()
    }

    fun initFB(){
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val query = rdb.limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<ExerciseData>()
            .setQuery(query,ExerciseData::class.java)
            .build()
        adapter = ExerciseAdapter(option)
        binding?.recyclerView?.layoutManager = layoutManager
        binding?.recyclerView?.adapter = adapter    //adapter에 연결
        adapter.itemClickListener = object : ExerciseAdapter.OnItemClickListener {
            override fun OnItemClick(holder: ExerciseAdapter.ViewHolder, view: View) {
                Toast.makeText(context,"gg",Toast.LENGTH_SHORT).show()
            }
        }
        adapter.startListening()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun init() {
        total = binding!!.minute.text.toString().toInt() *60 + binding!!.second.text.toString().toInt()
        var totaltotal:Int = 0
        var totaltotal2:Int = 0
        adapter.itemClickListener = object : ExerciseAdapter.OnItemClickListener {
            override fun OnItemClick(holder: ExerciseAdapter.ViewHolder, view: View) {
                stat = 0
                stop()
                var exerciseTimeText = holder.binding.ExerciseTime.text.toString()
                val spltext = exerciseTimeText.split(" ")

                binding!!.minute.text = String.format("%02d", spltext[0].toInt())
                binding!!.second.text = String.format("%02d", spltext[2].toInt())

                total = binding!!.minute.text.toString().toInt() * 60 + binding!!.second.text.toString().toInt()
                totaltotal = spltext[0].toInt()*60 + spltext[2].toInt()

                binding!!.progressBar1.setProgress(100)
                binding!!.progressBar2.setProgress(100)

                var restTimeText = holder.binding.RestTime.text.toString()
                val spltext2 = restTimeText.split(" ")
                binding!!.rminute.text = String.format("%02d", spltext2[0].toInt())
                binding!!.rsecond.text = String.format("%02d", spltext2[2].toInt())

                total2 = binding!!.rminute.text.toString().toInt() * 60 + binding!!.rsecond.text.toString().toInt()
                totaltotal2 = spltext2[0].toInt()*60 + spltext2[2].toInt()
            }
        }

        binding!!.startBtn.setOnClickListener {
            flag++
            if(totaltotal == 0 && totaltotal2 == 0){
                Toast.makeText(context, "운동 할 항목을 선택해주세요!", Toast.LENGTH_SHORT).show()
            }
            else if(flag == 1) {
                timerStart3(totaltotal, totaltotal2)
            }
            if(setCount == 0){
                flag = 0
            }
            else if(flag > 1){
                Toast.makeText(context, "이미 실행중 입니다!", Toast.LENGTH_SHORT).show()
                flag = 1
            }
        }
        binding!!.pasueBtn.setOnClickListener {
            pause()
        }
        binding!!.stopBtn.setOnClickListener {
            stop()
            totaltotal = 0
            totaltotal2 = 0
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun makeNotification() {
        val id = "MyChannel"
        val name = "MyApp"
        val notificationChannel = NotificationChannel(id,name, NotificationManager.IMPORTANCE_DEFAULT)
        // activity.NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableVibration(true)

        val builder = NotificationCompat.Builder(binding!!.root.context, id)
            .setSmallIcon(R.drawable.alarmimage)
            .setContentTitle("알람")
            .setAutoCancel(true)
        val manager =  context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
        val notification = builder.build()
        manager.notify(11, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timerStart3(totaltotal:Int, totaltotal2: Int){
        started = true

        setCount = binding?.setNum?.text?.toString()!!.toInt()
        if(setCount>0) {
            thread(start = true) {
                while(flag != 0) {
                    Thread.sleep(1000)

                    if (setCount == 0) {
                        makeNotification()
                        break
                    }

                    if (total > 0) {
                        total--
                        var percent = (total.toFloat() / totaltotal) * 100
                        binding!!.progressBar1.setProgress(percent.toInt())
                    }
                    else if (total == 0 && total2 > 0) {
                        total2--
                        var percent2 = (total2.toFloat() / totaltotal2) * 100
                        binding!!.progressBar2.setProgress(percent2.toInt())
                        if(setCount > 0){
                            setCount--
                            binding!!.setNum.text = setCount.toString()
                            total = totaltotal.toInt()
                            total2 = totaltotal2.toInt()
                            binding!!.progressBar1.setProgress(100)
                            binding!!.progressBar2.setProgress(100)
                        }
                        else{
                            makeNotification()
                            break
                        }
                    }
                    requireActivity().runOnUiThread {
                        if (total >= 0) {
                            binding!!.minute.text = String.format("%02d", (total / 60) % 60)
                            binding!!.second.text = String.format("%02d", (total) % 60)
                        }
                        if (total2 >= 0) {
                            binding!!.rminute.text = String.format("%02d", (total2 / 60) % 60)
                            binding!!.rsecond.text = String.format("%02d", (total2) % 60)
                        }
                    }

                }
            }
            if(setCount == 0) {
                flag = 0
                setCount = 0
                binding!!.setNum.text = setCount.toString()
                binding!!.minute.text = String.format("%02d", (total / 60) % 60)
                binding!!.second.text = String.format("%02d", (total) % 60)
                binding!!.rminute.text = String.format("%02d", (total2 / 60) % 60)
                binding!!.rsecond.text = String.format("%02d", (total2) % 60)
            }
        }
        else{
            Toast.makeText(context, "set수 다시 입력하세요", Toast.LENGTH_SHORT).show()
        }
    }

    fun pause() {
        started = false
        flag = 0
    }

    fun stop() {
        started = false
        total = 0
        binding!!.minute.text = "00"
        binding!!.second.text = "00"
        total2 = 0
        binding!!.rminute.text = "00"
        binding!!.rsecond.text = "00"
        flag = 0
    }

}
//초 있는 것만 2칸씩 줄어듬.. 무슨 문제일까...
