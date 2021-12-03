package com.example.heolle_beoltteok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class HomeFragment : Fragment() {
    val itemFragment2 = ItemFragment2()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val exercise = view.findViewById<ImageButton>(R.id.home_exercise_icon)
        val cooking = view.findViewById<ImageButton>(R.id.home_cooking_icon)
        val test = view.findViewById<ImageButton>(R.id.home_test_icon)

        exercise.setOnClickListener {
            From_Home_Transaction(ExerciseFragment())
        }
        cooking.setOnClickListener {
            From_Home_Transaction(CookFragment())
        }
        test.setOnClickListener {
            From_Home_Transaction(itemFragment2)
        }


        return view
    }

    fun From_Home_Transaction(fg:Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fg)
        transaction?.addToBackStack(null)
        transaction?.commit()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }


}