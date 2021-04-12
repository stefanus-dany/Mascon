package com.example.mascon.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.ListofUserAdapter
import com.example.mascon.Adapter.NotificationAdapter
import com.example.mascon.Model.NotificationModel
import com.example.mascon.Model.User
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var mNotif: MutableList<NotificationModel>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var pp: CircleImageView
    private lateinit var firebaseUser : FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val view: View = inflater.inflate(R.layout.fragment_notification, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        recyclerView = view.findViewById(R.id.rv_notif)
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mNotif = mutableListOf()

        notificationAdapter = NotificationAdapter()
        notificationAdapter.mContext = requireContext()
        notificationAdapter.mNotif = mNotif
        recyclerView.adapter = notificationAdapter

//        FirebaseDatabase.getInstance().reference.child("Users").child(data)
//            .child("invited")
//            .child(nameOfTopic.text.toString())
//            .child(podcastTitle.text.toString())
//            .child("invitedBy")
//            .child(firebaseUser.uid)
//            .setValue(true)
//
        val reference =
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(firebaseUser.uid)
                .child("invited").orderByChild("timeOnly")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mNotif.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(NotificationModel::class.java)
                    mNotif.add(value!!)
                    Log.i("notip",mNotif.toString())
                    notificationAdapter.notifyDataSetChanged()
                }
                //untuk reverse post berdasarkan waktu
                mNotif.reverse()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return view

//        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pp = view.findViewById(R.id.pp)
        //get profile picture
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
}