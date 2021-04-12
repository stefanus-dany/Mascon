package com.example.mascon.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.LiveChatModel
import com.example.mascon.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class LiveChatAdapter : RecyclerView.Adapter<LiveChatAdapter.ViewHolder> (){
    lateinit var mContext: Context
    lateinit var mChat : MutableList<LiveChatModel>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(mContext).inflate(R.layout.livechat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        holder.name_livechat.text = mChat[position].name
        holder.text_livechat.text = mChat[position].chat

        val email = mChat[position].email
        val imageProfile = storageReference.child("Users/$email/imgProfile")
        imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.imgProfile_livechat.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile_livechat : CircleImageView = itemView.findViewById(R.id.imgProfile_livechat)
        val name_livechat : TextView = itemView.findViewById(R.id.name_livechat)
        val text_livechat : TextView = itemView.findViewById(R.id.text_livechat)
    }
}