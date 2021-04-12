package com.example.mascon.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.Topic
import com.example.mascon.R
import com.example.mascon.Podcast.LivePodcast.LivePodcast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

//isi dari podcast nya
class PodcastAdapter:
    RecyclerView.Adapter<PodcastAdapter.ViewHolder>() {

    lateinit var mContextTopic: Context
    lateinit var firebaseUser: FirebaseUser
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    lateinit var mTopics: MutableList<Topic>

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.topic_thread, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
//        firebaseUser = FirebaseAuth.getInstance().currentUser
        val topic: Topic = mTopics[i]

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

//        Glide.with(mContextTopic).load(topic.imageProfile).into(viewHolder.image_profile)
//        Glide.with(mContextTopic).load().into(viewHolder.image_post)
        viewHolder.user_name.text = topic.fullname
        viewHolder.title_thread.text = topic.title
        viewHolder.topic_name.text = "t/" + topic.topicName
        viewHolder.desc_topic.text = topic.description
        viewHolder.tv_like.text = topic.like
        viewHolder.tv_comment.text = topic.comment
        viewHolder.tv_share.text = topic.share
        viewHolder.timeDate.text = topic.timeDate
//        viewHolder.tv_live.text = topic.live

        val namaTopik = topic.topicName
        val namaPodcast = topic.title
        val ownerTopic = topic.ownerTopic
        val linkMeet = topic.linkMeet

        FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(ownerTopic).child("linkMeet").get().addOnSuccessListener {
                if (linkMeet==it.value.toString()){
                    viewHolder.tv_live.visibility = View.VISIBLE
                    viewHolder.ic_live.visibility = View.VISIBLE
                    viewHolder.tv_outDate.visibility = View.GONE
                }
            }

        val imageRef = storageReference.child("Topics/$namaTopik/$namaPodcast/imgCreatePodcast")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_post.setImageBitmap(bitmap)
        }

        val email = topic.email

        val imageProfile = storageReference.child("Users/$email/imgProfile")
        imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_profile.setImageBitmap(bitmap)
        }

        viewHolder.itemView.setOnClickListener {
            if (viewHolder.tv_live.visibility==View.VISIBLE){
                FirebaseDatabase.getInstance().reference.child("Topics")
                    .child("listOfTopics")
                    .child(namaTopik)
                    .child("podcast")
                    .child(namaPodcast)
                    .child("live").get().addOnSuccessListener {
                        FirebaseDatabase.getInstance().reference.child("Topics")
                            .child("listOfTopics")
                            .child(namaTopik)
                            .child("podcast")
                            .child(namaPodcast)
                            .child("live").setValue((it.value.toString().toInt()+1).toString())
                    }

                val intent = Intent(mContextTopic, LivePodcast::class.java)
                intent.putExtra("topicName", namaTopik)
                intent.putExtra("podcastTitle", namaPodcast)
                intent.putExtra("ownerTopic", ownerTopic)
                intent.putExtra("linkMeet", linkMeet)
                mContextTopic.startActivity(intent)
            } else {
                Toast.makeText(mContextTopic, "Live podcast is out of date", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return mTopics.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_profile = itemView.findViewById<CircleImageView>(R.id.image_profile_menu_topic)
        var user_name = itemView.findViewById<TextView>(R.id.fullnamePodcast)
        var title_thread = itemView.findViewById<TextView>(R.id.title_thread)
        var topic_name = itemView.findViewById<TextView>(R.id.topic_name)
        var desc_topic = itemView.findViewById<TextView>(R.id.desc_topic)
        var image_post = itemView.findViewById<ImageView>(R.id.imagePost)
        var tv_like = itemView.findViewById<TextView>(R.id.tv_like)
        var tv_comment = itemView.findViewById<TextView>(R.id.tv_comment)
        var tv_share = itemView.findViewById<TextView>(R.id.tv_share)
        var tv_live = itemView.findViewById<TextView>(R.id.tv_live)
        var timeDate = itemView.findViewById<TextView>(R.id.timeDate)
        var ic_live = itemView.findViewById<ImageView>(R.id.ic_live)
        var tv_outDate = itemView.findViewById<TextView>(R.id.tv_outDate)
    }
}

