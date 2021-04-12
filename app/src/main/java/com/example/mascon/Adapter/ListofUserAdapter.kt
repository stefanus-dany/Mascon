package com.example.mascon.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.User
import com.example.mascon.Podcast.FragmentMakePodcast.SetPodcastNow
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class ListofUserAdapter : RecyclerView.Adapter<ListofUserAdapter.ViewHolder>() {

    lateinit var mContext: Context

    //user di adapter tipe list
    lateinit var mUsers: List<User>

    //user di Firebasenya
    lateinit var firebaseUser: FirebaseUser

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var check: Boolean = false


    //list untuk simpen data invite siapa aja
    //misalkan keluar dari setpodcastnow.kt, maka data invite di firebase bakalan hilang
    //misalkan ganti topic di setpodcastnow.kt, maka data invite di firebase  bakalan hilang

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.listofuser_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firebaseUser = FirebaseAuth.getInstance().currentUser
        //user di UserAdapter
        val user: User = mUsers[i]
        viewHolder.cbListofUser.visibility = View.VISIBLE
        viewHolder.email.text = user.email
        viewHolder.fullname.text = user.fullname

        val email = user.email

        val imageProfile = storageReference.child("Users/$email/imgProfile")
        imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_profile.setImageBitmap(bitmap)
        }

//        Glide.with(mContext).load(user.imageurl).into(viewHolder.image_profile)

        if (user.id == firebaseUser.uid) {
            viewHolder.cbListofUser.visibility = View.GONE
        }

        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("CheckBoxListOfUser")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewHolder.cbListofUser.isChecked = snapshot.child(user.id).exists()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        viewHolder.cbListofUser.setOnClickListener {
            if (viewHolder.cbListofUser.isChecked) {
                check = false
                klik(user.id)
            } else {
                check = true
                klik(user.id)
            }
        }
    }

    fun klik(user: String) {
        if (!check) {
            SetPodcastNow.saveData.add(user)
            SetPodcastNow.saveData.distinct()
            for (data in SetPodcastNow.saveData) {
                Log.i("Isi List(if)", data + " if")
            }
            Log.i("Isi List(if)", "--------")
            //nyimpen checkbox ke database untuk ditampilkan lagi
            FirebaseDatabase.getInstance().reference.child("CheckBoxListOfUser").child(user)
                .child("id")
                .setValue(user)
            check = true
        } else {
            SetPodcastNow.saveData.remove(user)
            for (data in SetPodcastNow.saveData) {
                Log.i("Isi List (else)", data)
            }
            Log.i("Isi List(else)", "--------")
            FirebaseDatabase.getInstance().reference.child("CheckBoxListOfUser").child(user)
                .child("id")
                .removeValue()
            check = false
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var email = itemView.findViewById<TextView>(R.id.emailListofUser)
        var fullname = itemView.findViewById<TextView>(R.id.fullnameListofUser)
        var image_profile = itemView.findViewById<CircleImageView>(R.id.image_profile_listofuser)
        var cbListofUser = itemView.findViewById<CheckBox>(R.id.cbListofUser)
    }
}

