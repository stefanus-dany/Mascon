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
import com.example.mascon.Fragment.HomeFragment
import com.example.mascon.Model.Topic
import com.example.mascon.R
import com.example.mascon.Podcast.LivePodcast.LivePodcast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

//isi dari podcast nya
class HomeAdapter(val mTopics: MutableList<Topic>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    lateinit var mContextTopic: Context
    lateinit var firebaseUser: FirebaseUser
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.home_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val topic: Topic = mTopics[i]

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

//        Glide.with(mContextTopic).load(topic.imageProfile).into(viewHolder.image_profile)
//        Glide.with(mContextTopic).load().into(viewHolder.image_post)
        viewHolder.fullnameHome.text = topic.fullname
        viewHolder.title_thread_home.text = topic.title
        viewHolder.topic_name_home.text = "t/" + topic.topicName
        viewHolder.desc_topic_home.text = topic.description
        viewHolder.tv_likeHome.text = topic.like
        viewHolder.tv_commentHome.text = topic.comment
        viewHolder.tv_shareHome.text = topic.share
//        viewHolder.tv_liveHome.text = topic.live
        viewHolder.timeDateHome.text = topic.timeDate

        val namaTopik = topic.topicName
        val namaPodcast = topic.title
        val ownerTopic = topic.ownerTopic
        val linkMeet = topic.linkMeet

        //buat ngecek kalo lagi ga live = out of date
        FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(ownerTopic).child("linkMeet").get().addOnSuccessListener {
                if (linkMeet==it.value.toString()){
                    viewHolder.tv_liveHome.visibility = View.VISIBLE
                    viewHolder.ic_liveHome.visibility = View.VISIBLE
                    viewHolder.tv_outOfDate.visibility = View.GONE
                }
            }


        val imageRef = storageReference.child("Topics/$namaTopik/$namaPodcast/imgCreatePodcast")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.imagePostHome.setImageBitmap(bitmap)
        }

        val email = topic.email

        val imageProfile = storageReference.child("Users/$email/imgProfile")
        imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_profile_home.setImageBitmap(bitmap)
        }

        viewHolder.itemView.setOnClickListener {
            if (viewHolder.tv_liveHome.visibility == View.VISIBLE){
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
        var image_profile_home = itemView.findViewById<CircleImageView>(R.id.image_profile_home)
        var fullnameHome = itemView.findViewById<TextView>(R.id.fullnameHome)
        var title_thread_home = itemView.findViewById<TextView>(R.id.title_thread_home)
        var topic_name_home = itemView.findViewById<TextView>(R.id.topic_name_home)
        var desc_topic_home = itemView.findViewById<TextView>(R.id.desc_topic_home)
        var imagePostHome = itemView.findViewById<ImageView>(R.id.imagePostHome)
        var tv_likeHome = itemView.findViewById<TextView>(R.id.tv_likeHome)
        var tv_commentHome = itemView.findViewById<TextView>(R.id.tv_commentHome)
        var tv_shareHome = itemView.findViewById<TextView>(R.id.tv_shareHome)
        var tv_liveHome = itemView.findViewById<TextView>(R.id.tv_liveHome)
        var timeDateHome = itemView.findViewById<TextView>(R.id.timeDateHome)
        var ic_liveHome = itemView.findViewById<ImageView>(R.id.ic_liveHome)
        var tv_outOfDate = itemView.findViewById<TextView>(R.id.tv_outOfDate)
    }
}

