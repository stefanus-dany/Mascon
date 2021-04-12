package com.example.mascon.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mascon.MainActivity
import com.example.mascon.R
import com.example.mascon.Topics.MakeTopics
import com.google.android.material.bottomsheet.BottomSheetDialog


class AddFragment : Fragment() {

    private lateinit var tvMakeTopic : TextView
    private lateinit var tvMakePodcast : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bikin onclik di addbox
//        tvMakeTopic = view.findViewById(R.id.tvMakeTopic)
//        tvMakePodcast = view.findViewById(R.id.tvMakePodcast)
//        tvMakeTopic.setOnClickListener {
//            startActivity(Intent(activity, MakeTopics::class.java))
//        }
    }
}