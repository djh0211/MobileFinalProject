package com.example.heolle_beoltteok

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.heolle_beoltteok.databinding.FragmentTestBinding
import com.example.heolle_beoltteok.databinding.FragmentUserIntroBinding
import com.example.heolle_beoltteok.databinding.FragmentUserTimerBinding
import com.example.heolle_beoltteok.databinding.UsertimerrowBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.thread

class UserTimerFragment : Fragment() {
    val viewModel: MyViewModel by activityViewModels()
    lateinit var rdb: DatabaseReference

    var TestInfo_ArrayList: ArrayList<UserItemInfo> = ArrayList()

    lateinit var layoutManager: LinearLayoutManager

    var binding: FragmentUserTimerBinding? = null
    lateinit var adapter: UserTimerAdapter

    var flag: Boolean = true
    var total = 0
    var started = true
    lateinit var myThread:Thread

    override fun onDestroyView() {
        super.onDestroyView()
        if(myThread.isAlive)
            stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentUserTimerBinding.inflate(layoutInflater, container, false)

        initRecycleView()
        firebaseDatainit()
        init()

        return binding!!.root
    }




    private fun initRecycleView() {
        adapter = UserTimerAdapter(TestInfo_ArrayList)
        binding!!.viewpager.adapter = adapter

    }

    private fun firebaseDatainit() {


        val firestore = FirebaseFirestore.getInstance()
        try {
            firestore.collection(viewModel.getValue())
                .get()
                .addOnSuccessListener { result ->
                    for (doc in result) {
                        TestInfo_ArrayList.add(doc.toObject(UserItemInfo::class.java))
                    }
                    Log.e("success", "success")
                    adapter.notifyDataSetChanged()

                }
                .addOnFailureListener {
                    Log.e("fail", "fail")
                }
        } catch (e: Exception) {
        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotification() {
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
    fun start() {
        flag = false
        started = true
        //sub thread
        myThread = thread(start = true) {
            var flag2 = true
            while (true) {
                Thread.sleep(1000)
                if(flag2 == false)  break
                if (!started) break

                val current = binding!!.viewpager.currentItem
                if(current == TestInfo_ArrayList.size-1 && binding!!.hour.text == "00" && binding!!.minute.text == "00" && binding!!.sec.text == "00")
                {
                    makeNotification()
//                    Toast.makeText(context,"pz",Toast.LENGTH_SHORT).show()
                    return@thread
                }
                total = total - 1
                activity!!.runOnUiThread {

                    if (binding!!.hour.text == "00" && binding!!.minute.text == "00" && binding!!.sec.text == "00") {
                        flag2 = false
                        var current = binding!!.viewpager.currentItem
                        binding!!.viewpager.setCurrentItem(current+1, false)
                        started=false
                        flag = true
                    }
                    //val current = binding!!.viewpager.currentItem

                    binding!!.hour.text = String.format("%02d", (total / 3600) % 60)
                    binding!!.minute.text = String.format("%02d", (total / 60) % 60)
                    binding!!.sec.text = String.format("%02d", total % 60)
                }

            }
        }

    }


    fun pause() {
        started = false
        flag = true
    }

    fun stop() {
        started = false
        total = 0
        binding!!.hour.text = "00"
        binding!!.minute.text = "00"
        binding!!.sec.text = "00"
        flag = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun init() {


        total = binding!!.hour.text.toString().toInt() * 3600 + binding!!.minute.text.toString().toInt() * 60 + binding!!.sec.text.toString().toInt()
        adapter.itemClickListener = object : UserTimerAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: UserTimerAdapter.ViewHolder,
                view: View,
                position: Int,
                hour: String,
                minute: String,
                sec: String
            ) {
                binding!!.hour.text = hour
                binding!!.minute.text = minute
                binding!!.sec.text = sec
                total = hour.toInt() * 3600 + minute.toInt() * 60 + sec.toInt()
                //holder.binding.nextButton.visibility = View.VISIBLE
            }

        }

        binding!!.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {


            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                var current = binding!!.viewpager.currentItem

                binding!!.hour.text = adapter.items[current].hour
                binding!!.minute.text = adapter.items[current].minute
                binding!!.sec.text = adapter.items[current].sec

                total = binding!!.hour.text.toString().toInt() * 3600 + binding!!.minute.text.toString().toInt() * 60 + binding!!.sec.text.toString().toInt()
                if(position!=0) {
                    binding!!.startBtn.performClick()

                }

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }


        })

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



}