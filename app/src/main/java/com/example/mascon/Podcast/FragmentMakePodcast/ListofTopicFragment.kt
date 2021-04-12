package com.example.mascon.Podcast.FragmentMakePodcast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.ListofTopicAdapter
import com.example.mascon.Model.ListofTopicModel
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListofTopicFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var listofTopicAdapter: ListofTopicAdapter
    private lateinit var btnDoneListOfTopic: Button
    private var listData: MutableList<ListofTopicModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listof_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        btnDoneListOfTopic = view.findViewById(R.id.btnDoneListOfTopic)
        recyclerView = view.findViewById(R.id.recycler_view_listTopic)
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.layoutManager = LinearLayoutManager(context)
        listofTopicAdapter = ListofTopicAdapter(listData)
        recyclerView.adapter = listofTopicAdapter
        listofTopicAdapter.mContext = requireContext()

        btnDoneListOfTopic.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val firebaseUser = auth.currentUser
        val userId = firebaseUser.uid
        val reference =
            FirebaseDatabase.getInstance().reference.child("Topics").child("listOfUsers")
                .child(userId).child("ownerOf").orderByChild("topic")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listData.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(ListofTopicModel::class.java)
                    listData.add(value!!)
                    listofTopicAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

}