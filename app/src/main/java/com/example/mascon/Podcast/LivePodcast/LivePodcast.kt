package com.example.mascon.Podcast.LivePodcast

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.LiveChatAdapter
import com.example.mascon.Adapter.LivePodcastAdapter
import com.example.mascon.Model.LiveChatModel
import com.example.mascon.Model.User
import com.example.mascon.R
import com.facebook.react.modules.core.PermissionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LivePodcast : AppCompatActivity(), JitsiMeetActivityInterface {
    private lateinit var podcastNameLive: TextView
    private lateinit var chatContent: EditText
    private lateinit var sendChat: ImageView
    private lateinit var back_livepodcast: ImageView
    private lateinit var topicNameLive: TextView
    private lateinit var timeLeftLive: TextView
    private lateinit var btnListen: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var livePodcastAdapter: LivePodcastAdapter
    private lateinit var mUsers: MutableList<User>
    //untuk update livecount di menutopic setelah kembali dari activity ini
//    private lateinit var recyclerView2: RecyclerView
//    private lateinit var podcastAdapter: PodcastAdapter

    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var liveChatAdapter: LiveChatAdapter
    private lateinit var mChat: MutableList<LiveChatModel>
    private lateinit var view: JitsiMeetView

    var topicName = ""
    var podcastTitle = ""
    var ownerTopic = ""
    var linkMeet = ""
    var checkListen = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_podcast)
        supportActionBar?.hide()
        //untuk update livecount di menutopic setelah kembali dari activity ini
//        podcastAdapter= PodcastAdapter()
//        recyclerView2 = findViewById(R.id.recycler_view_menutopic)
//        recyclerView2.layoutManager = LinearLayoutManager(applicationContext)
//        recyclerView2.adapter = podcastAdapter
//        recyclerView2.setHasFixedSize(true)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        btnListen = findViewById(R.id.btnListen)
        back_livepodcast = findViewById(R.id.back_livepodcast)
        chatContent = findViewById(R.id.chatContent)
        sendChat = findViewById(R.id.sendChat)
        podcastNameLive = findViewById(R.id.podcastNameLive)
        topicNameLive = findViewById(R.id.topicNameLive)
        timeLeftLive = findViewById(R.id.timeLeftLive)
        topicName = intent.getStringExtra("topicName").toString()
        podcastTitle = intent.getStringExtra("podcastTitle").toString()
        ownerTopic = intent.getStringExtra("ownerTopic").toString()
        linkMeet = intent.getStringExtra("linkMeet").toString()
        podcastNameLive.text = podcastTitle
        topicNameLive.text = "t/$topicName"
        mUsers = mutableListOf()
        livePodcastAdapter = LivePodcastAdapter()
        livePodcastAdapter.mUsers = mUsers
        livePodcastAdapter.mContext = applicationContext
        recyclerView = findViewById(R.id.rv_livePodcast)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = livePodcastAdapter

        //recyclerview untuk menampilkan data orang yang join live podcast di menutopic
        val reed = FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(ownerTopic!!)
            .child("listOfInvited").orderByChild("nameGuest")
//        Log.i("namaUsers", ownerTopic)
//        Toast.makeText(this, ownerTopic, Toast.LENGTH_LONG).show()
        reed.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(User::class.java)
                    mUsers.add(value!!)
                    livePodcastAdapter.notifyDataSetChanged()
//                    Log.i("namaUsers", mUsers.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        recyclerViewChat = findViewById(R.id.rv_liveChat)
        liveChatAdapter = LiveChatAdapter()
        mChat = mutableListOf()
        liveChatAdapter.mContext = applicationContext
        liveChatAdapter.mChat = mChat
        recyclerViewChat.layoutManager = LinearLayoutManager(applicationContext)
//        recyclerViewChat.setHasFixedSize(true)
        recyclerViewChat.adapter = liveChatAdapter

        back_livepodcast.setOnClickListener {
            finish()
        }

        sendChat.setOnClickListener {
            //random for LiveChat
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val idRandom = List(20) { alphabet.random() }.joinToString("")
            //read time
            val tgl = Date()
            val simpDate = SimpleDateFormat("kk:mm:ss")
            val timeOnly = simpDate.format(tgl)

            var email = ""
            var nama = ""

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(firebaseUser.uid).child("fullname").get().addOnSuccessListener {
                    nama = it.value.toString()

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(firebaseUser.uid).child("email").get().addOnSuccessListener {
                            email = it.value.toString()
                            val hashmap = HashMap<String, String>()
                            hashmap.put("email", email)
                            hashmap.put("name", nama)
                            hashmap.put("chat", chatContent.text.toString())
                            hashmap.put("timeOnly", timeOnly)
                            val refChat = FirebaseDatabase.getInstance().reference.child("LiveNow")
                                .child(ownerTopic!!)
                                .child("liveChat")
                                .child(idRandom)
                            refChat.setValue(hashmap)
                            chatContent.setText("")
                        }
                }
        }

        btnListen.setOnClickListener {
            btnListen.text = "Stop Listening"
            view = JitsiMeetView(this)
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom("https://meet.jit.si/" + linkMeet)
                .setAudioMuted(true)
                .setVideoMuted(true)
                .setAudioOnly(false)
                .setWelcomePageEnabled(false)
                .build()
            view.join(options)

            btnListen.setOnClickListener {
                checkListen = "btnListenClicked"
                btnListen.text = "Listen Now!"
//                Toast.makeText(this, "You're already in podcast live!",Toast.LENGTH_SHORT).show()
                view.leave()
//                setContentView(R.layout.activity_second)
            }
        }

        val rep = FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(ownerTopic!!)
            .child("liveChat").orderByChild("timeOnly")
        rep.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mChat.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(LiveChatModel::class.java)
                    mChat.add(value!!)
                    liveChatAdapter.notifyDataSetChanged()
                    recyclerViewChat.smoothScrollToPosition(liveChatAdapter.itemCount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (checkListen == "btnListenClicked") {
            view.leave()
        }
        Log.i("OnApa", "ondestroy")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (checkListen == "btnListenClicked") {
            view.leave()
        }
    }

    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        TODO("Not yet implemented")
    }
//
//    override fun onPause() {
//        Log.i("OnApa", "onpause")
////        Toast.makeText(context, "OnDestroy", Toast.LENGTH_SHORT).show()
//        super.onPause()
//    }
//
//    override fun onStop() {
//        Log.i("OnApa", "onstop")
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