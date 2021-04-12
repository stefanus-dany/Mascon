package com.example.mascon.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.User
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class LivePodcastAdapter : RecyclerView.Adapter<LivePodcastAdapter.ViewHolder>() {

    lateinit var mContext: Context

    //user di adapter tipe list
    lateinit var mUsers: List<User>

    //user di Firebasenya
    lateinit var firebaseUser: FirebaseUser

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    //list untuk simpen data invite siapa aja
    //misalkan keluar dari setpodcastnow.kt, maka data invite di firebase bakalan hilang
    //misalkan ganti topic di setpodcastnow.kt, maka data invite di firebase  bakalan hilang

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.livepodcast_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firebaseUser = FirebaseAuth.getInstance().currentUser
        //user di UserAdapter
        val user: User = mUsers[i]
        viewHolder.fullnameLivePodcast.text = user.nameGuest

        val email = user.email

        val imageProfile = storageReference.child("Users/$email/imgProfile")
        imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_profile_livepodcast.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fullnameLivePodcast = itemView.findViewById<TextView>(R.id.fullnameLivePodcast)
        var image_profile_livepodcast = itemView.findViewById<CircleImageView>(R.id.image_profile_livepodcast)
    }
}

