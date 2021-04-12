package com.example.mascon.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.MenuTopicModel
import com.example.mascon.Model.MyOwnTopicsModel
import com.example.mascon.R
import com.example.mascon.Topics.MenuTopic
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class MyJoinTopicsAdapter : RecyclerView.Adapter<MyJoinTopicsAdapter.ViewHolder>() {
    lateinit var mContextJoin : Context
    lateinit var mJoin : MutableList<MyOwnTopicsModel>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContextJoin).inflate(R.layout.myjointopic_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        viewholder.topicMyJoin.text = "t/" + mJoin[position].topic
        Log.e("mss", mJoin[position].topic)

        val topikNama = mJoin[position].topic
//        Toast.makeText(mContext, "Isinya : $topikNama" + mOwn.size, Toast.LENGTH_SHORT).show()
        val imageRefProfile = storageReference.child("Topics/$topikNama/profilTopic")
        imageRefProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewholder.photoMyJoin.setImageBitmap(bitmap)
        }

        viewholder.itemView.setOnClickListener {
            val intent = Intent(mContextJoin, MenuTopic::class.java)
            intent.putExtra("topicName", topikNama)
            mContextJoin.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mJoin.size
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val topicMyJoin : TextView = itemView.findViewById(R.id.topicMyJoin)
        val photoMyJoin : CircleImageView = itemView.findViewById(R.id.photoMyJoin)
    }
}