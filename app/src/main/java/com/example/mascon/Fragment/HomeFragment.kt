package com.example.mascon.Fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.HomeAdapter
import com.example.mascon.Adapter.PodcastAdapter
import com.example.mascon.MainActivity
import com.example.mascon.Model.MenuTopicModel
import com.example.mascon.Model.Topic
import com.example.mascon.Model.TopicHomeModel
import com.example.mascon.Podcast.MakePodcast
import com.example.mascon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragment : Fragment() {
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mTopics: MutableList<Topic>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var pp: CircleImageView
    lateinit var firebaseUser: FirebaseUser
    var topicFollowed = mutableListOf<String>()
    var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("OnApa", "oncreateview")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        pp = view.findViewById(R.id.pp)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
//        topicFollowed.clear()
        Log.i("OnApa", "onviewcreated")
        //firebase
        firebaseUser = FirebaseAuth.getInstance().currentUser
        recyclerView = view.findViewById(R.id.rv_home)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mTopics = mutableListOf()
        homeAdapter = HomeAdapter(mTopics)
        homeAdapter.mContextTopic = requireContext()
        recyclerView.adapter = homeAdapter

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid).child("email").get().addOnSuccessListener {
                email = it.value.toString()
                val imageProfile = storageReference.child("Users/$email/imgProfile")
                imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    pp.setImageBitmap(bitmap)
                }
            }

        //simpan nama topik yang user follow
        //memberof
        FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfUsers")
            .child(firebaseUser.uid)
            .child("memberOf").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap: DataSnapshot in snapshot.children) {
                        val value = snap.getValue(TopicHomeModel::class.java)
                        topicFollowed.add(value!!.topic)
                        homeAdapter.notifyDataSetChanged()
                        Log.i("hme", "di memberof1"+topicFollowed.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("hme", "cancel member of"+topicFollowed.toString())
                }

            })
        Log.i("hme", "di memberof2"+topicFollowed.toString())

        //ownerof
        FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfUsers")
            .child(firebaseUser.uid)
            .child("ownerOf").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap: DataSnapshot in snapshot.children) {
                        val value = snap.getValue(TopicHomeModel::class.java)
                        topicFollowed.add(value!!.topic)
                        homeAdapter.notifyDataSetChanged()
                        Log.i("hme", "di ownerof1"+topicFollowed.toString())
                    }
                    saveDataTimeline()
                    readPodcast()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("hme", "cancel owner of"+topicFollowed.toString())
                }

            })
        Log.i("hme", "di ownerof2"+topicFollowed.toString())

        Log.i("hme", "keluar readpodcast"+topicFollowed.toString())
        topicFollowed.clear()
        mTopics.clear()
    }

    fun saveDataTimeline() {
        Log.i("hme", "barumasuk read"+topicFollowed.toString())
        for (data in topicFollowed) {
            Log.i("hme", "di readpodcast"+topicFollowed.toString())
            //orderbychild buat ngurutin berdasarkan waktu post
            val reference =
                FirebaseDatabase.getInstance().reference.child("Topics")
                    .child("listOfTopics")
                    .child(data)
                    .child("podcast").orderByChild("timeOnly")

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val value = snapshot.getValue(Topic::class.java)
                        mTopics.add(value!!)
//                        homeAdapter.notifyDataSetChanged()

                        //simpan didatabase savedatatimeline biar ditimeline urutan berdasarkan waktu
                        //walaupun banyak topic yang difollow
                        val reference =
                            FirebaseDatabase.getInstance().reference.child("SaveDataTimeline").child(firebaseUser.uid)
                                .child(value.title)
                        val hashMap = HashMap<String, String>()
                        hashMap.put("title", value.title)
                        hashMap.put("description", value.description)
                        hashMap.put("topicName", value.topicName)
                        hashMap.put("like", value.like)
                        hashMap.put("comment", value.comment)
                        hashMap.put("share", value.share)
                        hashMap.put("live", value.live)
                        hashMap.put("fullname", value.fullname)
                        hashMap.put("email", value.email)
                        hashMap.put("timeDate", value.timeDate)
                        hashMap.put("timeOnly", value.timeOnly)
                        hashMap.put("linkMeet", value.linkMeet)
                        hashMap.put("ownerTopic", value.ownerTopic)

                        reference.setValue(hashMap)
                    }
                    //untuk reverse post berdasarkan waktu di menutopic
//                    mTopics.reverse()
//                    homeAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            FirebaseDatabase.getInstance().reference.child("SaveDataTimeline")
                .child(firebaseUser.uid).setValue(mTopics)
            Log.i("mTopics", "isinya "+topicFollowed.toString())
        }
        Log.i("hme", "barumasuk read2"+topicFollowed.toString())
    }

    fun readPodcast(){
        Log.i("mTopics", "isinya "+topicFollowed.toString())
        val reference =
            FirebaseDatabase.getInstance().reference.child("SaveDataTimeline")
                .child(firebaseUser.uid).orderByChild("timeOnly")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mTopics.clear()
                for (snapshot : DataSnapshot in dataSnapshot.children){
                    val value = snapshot.getValue(Topic::class.java)
                    mTopics.add(value!!)
                    homeAdapter.notifyDataSetChanged()
                }
                mTopics.reverse()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

//    override fun onDestroy() {
//        Log.i("OnApa", "ondestroy")
////        Toast.makeText(context, "OnDestroy", Toast.LENGTH_SHORT).show()
//        super.onDestroy()
//    }

//    override fun onDestroy() {
//        cek ondestroy di livepodcast, kalo ondestroy, brarti hrusnya code didalam ondestroy jalan. dan otomatis kehapus live countnya
//        super.onDestroy()
//        FirebaseDatabase.getInstance().reference.child("Topics")
//            .child("listOfTopics")
//            .child(topicName)
//            .child("podcast")
//            .child(podcastTitle)
//            .child("live").get().addOnSuccessListener {
//                FirebaseDatabase.getInstance().reference.child("Topics")
//                    .child("listOfTopics")
//                    .child(topicName)
//                    .child("podcast")
//                    .child(podcastTitle)
//                    .child("live").setValue((it.value.toString().toInt()-1).toString())
//            }
////        val menuTopic = MenuTopic()
////        menuTopic.podcastAdapter.notifyDataSetChanged()
//    }

    override fun onPause() {
        Log.i("OnApa", "onpause")
//        Toast.makeText(context, "onPause", Toast.LENGTH_SHORT).show()
        super.onPause()
    }

    override fun onStop() {
        Log.i("OnApa", "onstop")
//        topicFollowed.clear()
        super.onStop()
    }

    override fun onResume() {
        Log.i("OnApa", "onresume")
        super.onResume()
    }

    override fun onStart() {
        Log.i("OnApa", "start")
        super.onStart()
    }
}