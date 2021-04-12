package com.example.mascon.Podcast.FragmentMakePodcast

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mascon.Model.User
import com.example.mascon.Podcast.MakePodcast
import com.example.mascon.R
import com.example.mascon.Topics.MenuTopic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class SetPodcastNow : Fragment(), MakePodcast.IOnBackPressed {

    private lateinit var topicsSetPodcastNow: TextView
    private lateinit var podcastTitle: EditText
    private lateinit var podcastDescription: EditText
    private var mContext: Context? = null
    private lateinit var tvAddImage: TextView
    private lateinit var ivAddImage: ImageView
    private lateinit var tvRemoveImage: TextView
    private lateinit var btnInvite: ImageButton
    private lateinit var nameOfTopic: TextView
    var nameOfTopicTextSaved = nameOfTopicText
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var imageURI: Uri? = null
    private lateinit var btnCreatePodcastNow: Button
    var dataCheckBox: MutableList<String> = mutableListOf()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser
    lateinit var fulName: String
    lateinit var email: String
    lateinit var dateOnly: String
    lateinit var timeOnly: String
    private lateinit var linkMeet : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_podcast_now, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastTitle = view.findViewById(R.id.podcastTitle)
        podcastDescription = view.findViewById(R.id.podcastDescription)
        ivAddImage = view.findViewById(R.id.ivAddImage)
        tvAddImage = view.findViewById(R.id.tvAddImage)
        tvRemoveImage = view.findViewById(R.id.tvRemoveImage)
        topicsSetPodcastNow = view.findViewById(R.id.topicsSetPodcastNow)
        nameOfTopic = view.findViewById(R.id.nameOfTopic)
        btnInvite = view.findViewById(R.id.btnInvite)
        btnCreatePodcastNow = view.findViewById(R.id.btnCreatePodcastNow)
        mContext = context
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference


        //linkMeetLivePodcast
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        linkMeet = List(20) { alphabet.random() }.joinToString("")


        //read date and time
        val calendar = Calendar.getInstance().time
        val timeDate = DateFormat.getDateTimeInstance().format(calendar)
        dateOnly = DateFormat.getDateInstance().format(calendar)
//        val timeOnly = DateFormat.getTimeInstance().format(calendar)

        val tgl = Date()
        val simpDate = SimpleDateFormat("kk:mm:ss")
        timeOnly = simpDate.format(tgl)

        //read database fullname dan imageProfile dari user
        val baca = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid)
        baca.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    email = value.email
                }
                if (value != null) {
                    fulName = value.fullname
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //jika add image ada fotonya, do this if
        if (imageURI != null) {
            ivAddImage.visibility = View.VISIBLE
            tvRemoveImage.visibility = View.VISIBLE
            ivAddImage.setImageURI(imageURI)
        }
//        if (savedInstanceState != null) {
//            ivAddImage.setImageURI(savedInstanceState.getParcelable("saveimage"))
//            ivAddImage.visibility = View.VISIBLE
//            tvRemoveImage.visibility = View.VISIBLE
//            imageURI = savedInstanceState.getParcelable("saveimage")
//        }

        nameOfTopic.text = nameOfTopicText

//        untuk cek apakah ganti topik? jika iya, maka array saveData akan dihapus dan database checkbox akan dihapus
        if (nameOfTopicText != nameOfTopicTextSaved) {
            saveData = mutableListOf()
            FirebaseDatabase.getInstance().reference.child("CheckBoxListOfUser").removeValue()
            nameOfTopicTextSaved = nameOfTopicText
        }

        //jika ingin membuka listoftopic buat di field topic
        topicsSetPodcastNow.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.flSetPodcast,
                    ListofTopicFragment(),
                    ListofTopicFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }

        }

        btnInvite.setOnClickListener {
            if (nameOfTopicText != null) {
                fragmentManager?.beginTransaction()?.apply {
                    replace(
                        R.id.flSetPodcast,
                        ListofUser(),
                        ListofUser::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }

            } else {
                Toast.makeText(
                    this.context,
                    "Choose your topic first!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //menambahkan gambar di field add image
        tvAddImage.setOnClickListener {
            try {
                mContext?.let { it1 ->
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3, 3)
                        .setMaxZoom(5)
                        .start(it1, this)
                };
            } catch (e: Exception) {
                Toast.makeText(mContext, "Errornya adalah " + e, Toast.LENGTH_SHORT).show()
            }
        }

        //menghapus gambar di field add image
        tvRemoveImage.setOnClickListener {
            ivAddImage.setImageURI(null)
            imageURI = null
            ivAddImage.visibility = View.GONE
            tvRemoveImage.visibility = View.GONE
        }


        btnCreatePodcastNow.setOnClickListener {

            if (nameOfTopic.text.toString().trim().isEmpty()) {
                nameOfTopic.error = "Please enter the topic"
                Toast.makeText(this.context, "Choose your topic first!", Toast.LENGTH_SHORT).show()
                nameOfTopic.requestFocus()
                return@setOnClickListener
            }

            if (podcastTitle.text.toString().trim().isEmpty()) {
                podcastTitle.error = "Please enter the title"
                podcastTitle.requestFocus()
                return@setOnClickListener
            }

            if (podcastDescription.text.toString().trim().isEmpty()) {
                podcastDescription.error = "Please enter the description"
                podcastDescription.requestFocus()
                return@setOnClickListener
            }

            val pd = ProgressDialog(mContext)
            pd.setTitle("Loading...")
            pd.setMessage("Please wait..")
            pd.show()

            if (ivAddImage.visibility != View.GONE) {
                uploadPicture()
            }

            val reference =
                FirebaseDatabase.getInstance().reference.child("Topics").child("listOfTopics")
                    .child(nameOfTopic.text.toString()).child("podcast")
                    .child(podcastTitle.text.toString())
            val hashMap = HashMap<String, String>()
            hashMap.put("title", podcastTitle.text.toString())
            hashMap.put("description", podcastDescription.text.toString())
            hashMap.put("topicName", nameOfTopic.text.toString())
            hashMap.put("like", 0.toString())
            hashMap.put("comment", 0.toString())
            hashMap.put("share", 0.toString())
            hashMap.put("live", 0.toString())
            hashMap.put("fullname", fulName)
            hashMap.put("email", email)
            hashMap.put("timeDate", timeDate.toString())
            hashMap.put("timeOnly", timeOnly)
            hashMap.put("linkMeet", linkMeet)
            hashMap.put("ownerTopic", firebaseUser.uid)

            val topik =
                FirebaseDatabase.getInstance().reference.child("Topics").child("listOfUsers")
                    .child(firebaseUser.uid)
                    .child("ownerOf")
                    .child(nameOfTopic.text.toString())
                    .child("podcastTitle")
            topik.setValue(podcastTitle.text.toString())

            val hashmap2 = HashMap<String, String>()
            hashmap2.put("topic", nameOfTopic.text.toString())
            hashmap2.put("podcastTitle", podcastTitle.text.toString())
            hashmap2.put("linkMeet", linkMeet)

            val reff = FirebaseDatabase.getInstance().reference.child("LiveNow")
                .child(firebaseUser.uid)
            reff.setValue(hashmap2)
//            hashMap.put(
//                "imageUrl",
//                "https://firebasestorage.googleapis.com/v0/b/mascon-2744b.appspot.com/o/defaultPhotoProfil.png?alt=media&token=9da04419-ebdc-4840-82b9-60a2aa72ccb2"
//            )

            //read database checkbox
            val cb: DatabaseReference = FirebaseDatabase.getInstance().reference
                .child("CheckBoxListOfUser")
            cb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val value = snapshot.getValue(User::class.java)
                        dataCheckBox.add(value!!.id)
                    }

//                    viewHolder.cbListofUser.isChecked = snapshot.child(userId).exists()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            reference.setValue(hashMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    inviteOrInvited()
                    val intent = Intent(this.context, MenuTopic::class.java)
                    intent.putExtra("topicName", nameOfTopic.text.toString())
                    startActivity(intent)
                    activity?.finish()
                    nameOfTopicText = null
                    activity?.finish()
                    pd.dismiss()
                } else {
                    Toast.makeText(this.context, "Adding podcast is failed", Toast.LENGTH_SHORT)
                        .show()
                }

            }


        }
    }

    fun inviteOrInvited() {
        //buat invite
        for (data in dataCheckBox) {
            FirebaseDatabase.getInstance().reference.child("Topics").child("listOfTopics")
                .child(nameOfTopic.text.toString()).child("podcast")
                .child(podcastTitle.text.toString())
                .child("invite").child(data).setValue(true)
        }

        //buat invited
        for (data in dataCheckBox) {
            val reff = FirebaseDatabase.getInstance().reference.child("Users").child(data)
                .child("invited")
                .child(nameOfTopic.text.toString())

            val hashmap = HashMap<String, String>()
            hashmap.put("podcastTitle", podcastTitle.text.toString())
            hashmap.put("invitedBy", firebaseUser.uid)
            hashmap.put("fullname", fulName)
            hashmap.put("topicName", nameOfTopic.text.toString())
            hashmap.put("descPodcast", podcastDescription.text.toString())
            hashmap.put("dateNotif", dateOnly)
            hashmap.put("email", email)
            hashmap.put("linkMeet", linkMeet)
            hashmap.put("timeOnly", timeOnly)
            reff.setValue(hashmap)
        }
    }

    //method untuk crop image di field addimage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // && resultCode!= RESULT_CANCELED
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                imageURI = result.uri
                ivAddImage.setImageURI(imageURI)
                ivAddImage.visibility = View.VISIBLE
                tvRemoveImage.visibility = View.VISIBLE
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this.context, "Error pada Crop Image : '$error'", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

//    uploadPicture()
//                Handler().postDelayed({
//                    downloadPicture()
//
//                }, 4000)

    private fun uploadPicture() {
        if (imageURI != null) {
            val namaTopic = nameOfTopic.text.toString()
            val namaPodcast = podcastTitle.text.toString()
            val tmp: StorageReference =
                storageReference.child("Topics/$namaTopic/$namaPodcast/imgCreatePodcast")
            imageURI?.let { tmp.putFile(it) }
        }
    }

    private fun downloadPicture() {
        val imageRef = storageReference.child("Tmp").child("imgsave")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            ivAddImage.setImageBitmap(bitmap)

        }

    }

    //method ini untuk mengambil data dari fragment MakePodcast.kt
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
////        if (savedInstanceState != null) {
////            val descFromBundle = savedInstanceState.getString(EXTRA_DESCRIPTION)
////            description = descFromBundle
////        }
//
//        if (arguments != null) {
//            val name = arguments?.getString("ValueOfnameoftopic")
//            nameOfTopic?.text = name
//            nameOfTopicText = name
//        }
//    }

    //    method jika back button diklik, maka melakukan aksi
    override fun onBackPressed(): Boolean {
        nameOfTopicText = null
        return true
        //        startActivity(Intent(context, MainActivity::class.java))
//        saveData = arrayListOf()

    }

    companion object {
        var nameOfTopicText: String? = null
        var saveData: MutableList<String> = mutableListOf()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelable("imagesave", imageURI)
//    }

}