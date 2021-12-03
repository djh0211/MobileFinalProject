package com.example.heolle_beoltteok

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heolle_beoltteok.databinding.ActivityMainBinding
import com.example.heolle_beoltteok.databinding.AddDialog3Binding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var tempList = ArrayList<String>()
    var i = 0
    private var time = 0
    private var timerTask: Timer? = null      // null을 허용
    private var isRunning = false
    private var lap = 1
    val itemFragment2 = ItemFragment2()
    lateinit var dialogBinding: AddDialog3Binding
    lateinit var binding:ActivityMainBinding
    lateinit var adapter: navAdapter
    val rdb = FirebaseDatabase.getInstance().getReference("datas2/schedule")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        dialogBinding = AddDialog3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        initrRecyclerView()
        init()
    }

    private fun initrRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val query = rdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<scheduleData>()
                .setQuery(query, scheduleData::class.java)
                .build()

        adapter = navAdapter(option)
        binding.recyclerView.adapter  = adapter
        adapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
            }

        }
        return super.onOptionsItemSelected(item)
    }




private fun init() {
    adapter.itemClickListener = object :navAdapter.OnItemClickListener {
        override fun OnItemClick(holder: navAdapter.ViewHolder, view: View) {
            rdb.child(holder.binding.textView8.text.toString()).removeValue()

        }
    }
    val addBtn = findViewById<Button>(R.id.button3)

    addBtn.setOnClickListener {

        val mBuilder = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .setTitle("일정 추가")
        val mAlertDialog = mBuilder.show()


        val okButton = dialogBinding.addDialogAddButton
        val noButton = dialogBinding.addDialogCancleButton
        val testName = dialogBinding.addDialogCookingName

        okButton.setOnClickListener {
            val data = hashMapOf<String,String>("schedule" to testName.text.toString())
            rdb.child(testName.text.toString()).setValue(data)
            adapter.notifyDataSetChanged()
            testName.text.clear()
            mAlertDialog.dismiss()
            (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)
            i++
        }
        noButton.setOnClickListener {

            (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)

            mAlertDialog.dismiss()
        }


    }
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.addOnBackStackChangedListener {
            var fg = supportFragmentManager.findFragmentById(R.id.frameLayout)
            var fg_str = fg.toString().split("{")[0]
            if (fg_str == "ExerciseFragment"){
                bottomNavBar.menu.getItem(1).isChecked = true
            } else if (fg_str == "CookFragment"){
                bottomNavBar.menu.getItem(2).isChecked = true
            } else if (fg_str == "TestFragment"){
                bottomNavBar.menu.getItem(3).isChecked = true
            }else if (fg_str == "UserMenuFragment"){
                bottomNavBar.menu.getItem(4).isChecked = true
            }

        }

        replaceFragment(HomeFragment())

        bottomNavBar.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home_page -> {
                    replaceFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.exercise_page -> {
                    replaceFragment(ExerciseFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.cooking_page -> {
                    replaceFragment(CookFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.test_page -> {
                    replaceFragment(itemFragment2)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.usertimer_page -> {
                    replaceFragment(UserMenuFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }
}




