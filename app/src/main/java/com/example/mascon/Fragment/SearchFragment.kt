package com.example.mascon.Fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.SearchAdapter
import com.example.mascon.Model.SearchModel
import com.example.mascon.Model.User
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var mTopics: MutableList<SearchModel>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var pp: CircleImageView
    var search_bar: EditText? = null
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        (activity as AppCompatActivity).supportActionBar?.hide()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        //ini bikin error?
        search_bar = view.findViewById(R.id.search_bar)

        mTopics = mutableListOf()

        //bikin error ga?
        searchAdapter = SearchAdapter(mTopics)
        searchAdapter.mContext = requireContext()
//        searchAdapter.mTopics = mTopics
        recyclerView.adapter = searchAdapter

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
    }

    fun searchUsers(s: String) {
        val query: Query =
            FirebaseDatabase.getInstance().getReference("Topics").child("listOfTopics")
                .orderByChild("topic")
                .startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mTopics.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    //mungkin error disini
                    val value = snapshot.getValue(SearchModel::class.java)
                    mTopics.add(value!!)
                    searchAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("Topics").child("listOfTopics")
            .orderByChild("topic")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (search_bar?.text.toString().equals("")) {
                    mTopics.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        //mungkin error disini
                        val value = snapshot.getValue(SearchModel::class.java)
                        mTopics.add(value!!)
                        searchAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pp = view.findViewById(R.id.pp)
        var email = ""

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid).child("email").get().addOnSuccessListener {
                email = it.value.toString()
                val imageProfile = storageReference.child("Users/$email/imgProfile")
                imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    pp.setImageBitmap(bitmap)
                }
            }
    }

//    override fun onPause() {
//        Log.i("OnApa", "onpause")
//        Toast.makeText(context, "onPause", Toast.LENGTH_SHORT).show()
//        super.onPause()
//    }
//
//    override fun onStop() {
//        Log.i("OnApa", "onstop")
////        topicFollowed.clear()
//        super.onStop()
//    }
//
//    override fun onResume() {
//        Log.i("OnApa", "onresume")
//        super.onResume()
//    }
//
//    override fun onStart() {
//        Log.i("OnApa", "start")
//        super.onStart()
//    }
}