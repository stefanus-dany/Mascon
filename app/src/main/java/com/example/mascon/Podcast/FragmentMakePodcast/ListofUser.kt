package com.example.mascon.Podcast.FragmentMakePodcast

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.ListofUserAdapter
import com.example.mascon.Model.User
import com.example.mascon.R
import com.google.firebase.database.*
import java.util.*

class ListofUser : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listofUserAdapter: ListofUserAdapter
    private lateinit var btnDoneListOfUser: Button
    private lateinit var mUsers: MutableList<User>
    var search_bar: EditText? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_listof_user, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()
        btnDoneListOfUser = view.findViewById(R.id.btnDoneListOfUser)
        recyclerView = view.findViewById(R.id.recycler_view_listOfUser)
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        //ini bikin error?
        search_bar = view.findViewById(R.id.search_bar_listOfUser)

        mUsers = mutableListOf()

        //bikin error ga?
        listofUserAdapter = ListofUserAdapter()
        listofUserAdapter.mContext = requireContext()
        listofUserAdapter.mUsers = mUsers
        recyclerView.adapter = listofUserAdapter

        btnDoneListOfUser.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        readUsers()
        search_bar?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().toLowerCase(Locale.ROOT))
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        return view

//        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    fun searchUsers(s: String) {
        val query: Query =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("email")
                .startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    //mungkin error disini
                    val user = snapshot.getValue(User::class.java)
                    mUsers.add(user!!)
                    listofUserAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (search_bar?.text.toString().equals("")) {
                    mUsers.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        //mungkin error disini
                        val user = snapshot.getValue(User::class.java)
                        mUsers.add(user!!)
                        listofUserAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}