package com.example.mascon.Topics

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mascon.Adapter.PodcastAdapter
import com.example.mascon.MainActivity
import com.example.mascon.Model.MenuTopicModel
import com.example.mascon.Model.Topic
import com.example.mascon.Podcast.MakePodcast
import com.example.mascon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class MenuTopic : AppCompatActivity() {

    lateinit var podcastAdapter: PodcastAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mTopics: MutableList<Topic>
    private lateinit var coverTopic: ImageView
    private lateinit var profilPictureTopic: CircleImageView
    private lateinit var menu_topic_name: TextView
    private lateinit var menu_memberCount: TextView
    private lateinit var btnJoin: Button
    private lateinit var back_menutopic: ImageView
    private lateinit var addPodcast: FloatingActionButton
    private lateinit var menu_desc: TextView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    lateinit var firebaseUser: FirebaseUser
    private var topicName: String? = null
    private var hakAkses = "member"
    var sizeData = 0

    //punya cover
    var imageURI: Uri? = null

    //punya photo profile
    var imageURIProfile: Uri? = null
    var ppORcover = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_topic)
        supportActionBar?.hide()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        //ambil topicName dari intent
        topicName = intent.getStringExtra("topicName")

        //firebase
        firebaseUser = FirebaseAuth.getInstance().currentUser
        //
        addPodcast = findViewById(R.id.addPodcast)
        back_menutopic = findViewById(R.id.back_menutopic)
        coverTopic = findViewById(R.id.coverTopic)
        profilPictureTopic = findViewById(R.id.profilPictureTopic)
        menu_topic_name = findViewById(R.id.menu_topic_name)
        menu_memberCount = findViewById(R.id.menu_memberCount)
        btnJoin = findViewById(R.id.btnJoin)
        menu_desc = findViewById(R.id.menu_desc)
        recyclerView = findViewById(R.id.recycler_view_menutopic)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        mTopics = mutableListOf()

        //bikin error ga?
        podcastAdapter = PodcastAdapter()
        podcastAdapter.mTopics = mTopics
        podcastAdapter.mContextTopic = this
        recyclerView.adapter = podcastAdapter
        //untuk refresh setelah back dari live podcast agar livecount realtime
        podcastAdapter.notifyDataSetChanged()

        download()
        isJoined()
        memberCount()
//        saveMemberCount()
        addPodcast.setOnClickListener {
            val intent = Intent(this, MakePodcast::class.java)
            intent.putExtra("nameOfTopic", topicName)
            startActivity(intent)
            finish()
        }

        //setelah activity make topic
        //jika membuat topic/podcast baru, maka tampilan di menu topic akan seperti dibawah ini
        if (topicName != null) {
            val reference =
                FirebaseDatabase.getInstance().getReference("Topics").child("listOfTopics")
                    .child(topicName!!)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue(MenuTopicModel::class.java)
                    menu_topic_name.text = value?.topic
                    menu_desc.text = "Description :\n" + value?.description
                    menu_memberCount.text = "$sizeData Member"
                    podcastAdapter.notifyDataSetChanged()

                    if (firebaseUser.uid == value?.ownerTopic) {
                        btnJoin.visibility = View.INVISIBLE
                        addPodcast.visibility = View.VISIBLE
                        btnJoin.text = "Delete Topic"
                        btnJoin.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.warnaUnjoin));
                        hakAkses = "owner"
                    }

                }


                override fun onCancelled(error: DatabaseError) {

                }
            })
            //else dibawah ini jika dari search topics
        } else {

        }
        readPodcast()

        back_menutopic.setOnClickListener {
            finish()
        }

        btnJoin.setOnClickListener {
            if (hakAkses == "owner") {
//                deleteTopic()

            } else {
                if ((btnJoin.text.toString() == "Join")) {
                    FirebaseDatabase.getInstance().reference.child("Topics")
                        .child("listOfUsers")
                        .child(firebaseUser.uid)
                        .child("memberOf")
                        .child(topicName!!).child("topic").setValue(topicName)

                    FirebaseDatabase.getInstance().reference.child("Topics")
                        .child("listOfTopics")
                        .child(topicName!!)
                        .child("listMember")
                        .child(firebaseUser.uid).setValue(true)

                } else {
                    FirebaseDatabase.getInstance().reference.child("Topics")
                        .child("listOfUsers")
                        .child(firebaseUser.uid)
                        .child("memberOf")
                        .child(topicName!!).child("topic").removeValue()

                    FirebaseDatabase.getInstance().reference.child("Topics")
                        .child("listOfTopics")
                        .child(topicName!!)
                        .child("listMember")
                        .child(firebaseUser.uid).removeValue()
                }
            }
        }

        profilPictureTopic.setOnClickListener {
            if (btnJoin.text == "Delete Topic") {
                try {
                    ppORcover = "profilePicture"
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3, 3)
                        .setMaxZoom(5)
                        .start(this);
                } catch (e: Exception) {
                    Toast.makeText(this, "Errornya adalah " + e, Toast.LENGTH_SHORT).show()
                }
            }
        }

        coverTopic.setOnClickListener {
            if (btnJoin.text == "Delete Topic") {
                try {
                    ppORcover = "coverPicture"
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(8, 3)
                        .setMaxZoom(5)
                        .start(this);
                } catch (e: Exception) {
                    Toast.makeText(this, "Errornya adalah " + e, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun memberCount() {
        val listData = mutableListOf<String>()
        val ref = FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfTopics").child(topicName!!)
            .child("listMember")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    listData.add(data.toString())
                }
                sizeData = listData.size + 1
                listData.clear()
                saveMemberCount()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //save member count to database
    private fun saveMemberCount() {
        val ref = FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfTopics").child(topicName!!).child("memberCount")
        ref.setValue(sizeData.toString())
    }

    private fun deleteTopic() {
        //mutable list buat mendata user yang join topic tertentu dan menghapusnya di listofusers

        val ref = FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfTopics").child(topicName!!)
            .child("listMember")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    //untuk mengambil id user dari listMember topic
                    val a = data.toString()
                    val b = a.split("=").toTypedArray()
                    val c = b[1].split(",").toTypedArray()
                    val result = c[0].trim()
                    //hapus di memberOf
                    FirebaseDatabase.getInstance().reference.child("Topics")
                        .child("listOfUsers")
                        .child(result)
                        .child("memberOf")
                        .child(topicName!!).removeValue()
                    Log.i("database", "awal " + result)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        //hapus di listOfTopics
        FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfTopics").child(topicName!!).removeValue()

        //hapus ownerOf
        FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfUsers")
            .child(firebaseUser.uid)
            .child("ownerOf")
            .child(topicName!!).removeValue()

        //hapus memberOf
//        if (deleteUser == null){
//            Toast.makeText(this, "delete user kosong", Toast.LENGTH_SHORT).show()
//            Log.i("database", "delete user kosong")
//        }

//        for (dataa in deleteUser) {
//            Log.i("database", "lewat")
//            FirebaseDatabase.getInstance().reference.child("Topics")
//                .child("listOfUsers")
//                .child(dataa)
//                .child("memberOf")
//                .child(topicName!!).removeValue()
//            Log.i("database", "perulagan")
//        }


        startActivity(Intent(this@MenuTopic, MainActivity::class.java))
    }

    fun isJoined() {
        val reference = FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfUsers")
            .child(firebaseUser.uid).child("memberOf")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (hakAkses == "member") {
                    if (snapshot.child(topicName!!).exists()) {
                        btnJoin.text = "Leave"
                        btnJoin.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.warnaUnjoin));
                    } else {
                        btnJoin.text = "Join"
                        btnJoin.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.warnaUtama));
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val foto = result.uri
                if (ppORcover == "coverPicture") {
                    imageURI = foto
                    coverTopic.setImageURI(imageURI)
                } else {
                    imageURIProfile = foto
                    profilPictureTopic.setImageURI(imageURIProfile)
                }

                upload()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error pada Crop Image : '$error'", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun upload() {
        //upload image
        if (ppORcover == "coverPicture") {
            val tmp: StorageReference =
                storageReference.child("Topics/$topicName/coverTopic")
            imageURI?.let { tmp.putFile(it) }

        } else {
            val tmp: StorageReference =
                storageReference.child("Topics/$topicName/profilTopic")
            imageURIProfile?.let { tmp.putFile(it) }
        }
    }

    fun download() {
        //downloadimage
        val imageRef = storageReference.child("Topics/$topicName/coverTopic")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            coverTopic.setImageBitmap(bitmap)
        }

        val imageRefProfile = storageReference.child("Topics/$topicName/profilTopic")
        imageRefProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            profilPictureTopic.setImageBitmap(bitmap)
        }
    }

    fun readPodcast() {
        //orderbychild buat ngurutin berdasarkan waktu post
        val reference =
            FirebaseDatabase.getInstance().reference.child("Topics")
                .child("listOfTopics")
                .child(topicName!!).child("podcast").orderByChild("timeOnly")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mTopics.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(Topic::class.java)
                    mTopics.add(value!!)
                    podcastAdapter.notifyDataSetChanged()
                }
                //untuk reverse post berdasarkan waktu di menutopic
                mTopics.reverse()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        super.onDestroy()
//        finish()
//    }
}