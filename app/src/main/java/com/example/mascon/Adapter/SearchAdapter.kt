package com.example.mascon.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.SearchModel
import com.example.mascon.R
import com.example.mascon.Topics.MenuTopic
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(var mTopics: MutableList<SearchModel>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    lateinit var mContext : Context
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.topic_search_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        viewHolder.topicNameSearch.text = "t/" + mTopics[position].topic
        viewHolder.memberCountSearch.text = mTopics[position].memberCount + " Member(s)"

        val topiknama = mTopics[position].topic
        val imageRefProfile = storageReference.child("Topics/$topiknama/profilTopic")
        imageRefProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_topic.setImageBitmap(bitmap)
        }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(mContext, MenuTopic::class.java)
            intent.putExtra("topicName", topiknama)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mTopics.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image_topic : CircleImageView = itemView.findViewById(R.id.image_topic)
        val topicNameSearch : TextView = itemView.findViewById(R.id.topicNameSearch)
        val memberCountSearch : TextView = itemView.findViewById(R.id.memberCountSearch)
    }

}