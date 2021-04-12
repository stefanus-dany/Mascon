package com.example.mascon.Fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Adapter.MyJoinTopicsAdapter
import com.example.mascon.Adapter.MyOwnTopicsAdapter
import com.example.mascon.Login
import com.example.mascon.Model.LiveNowModel
import com.example.mascon.Model.MyOwnTopicsModel
import com.example.mascon.Model.Topic
import com.example.mascon.Model.User
import com.example.mascon.R
import com.example.mascon.Podcast.LivePodcast.LiveNowDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import kr.co.prnd.readmore.ReadMoreTextView


class ProfileFragment : Fragment() {

    private lateinit var imgProfile: CircleImageView
    private lateinit var nameProfile: TextView
    private lateinit var emailProfile: TextView
    private lateinit var mOwn: MutableList<MyOwnTopicsModel>
    private lateinit var mJoin: MutableList<MyOwnTopicsModel>
    private lateinit var recyclerViewOwn: RecyclerView
    private lateinit var recyclerViewJoin: RecyclerView
    private lateinit var myOwnTopicsAdapter: MyOwnTopicsAdapter
    private lateinit var myJoinTopicsAdapter: MyJoinTopicsAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var logout: ImageView
    private var email: String = ""
    private var podcastTitleProfile: String? = null
    var nameOfTopicProfile: String? = null
    private var linkMeet: String? = null
    lateinit var rvMyLivePodcast: LinearLayout
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var fullnamePodcastProfile: TextView
    private lateinit var timeDateProfile: TextView
    private lateinit var title_threadProfile: TextView
    private lateinit var topic_nameProfile: TextView
    private lateinit var desc_topicProfile: ReadMoreTextView
    private lateinit var imagePostProfile: ImageView
    private lateinit var image_profile_menu_topicProfile: ImageView
    private lateinit var tv_likeProfile: TextView
    private lateinit var tv_commentProfile: TextView
    private lateinit var tv_shareProfile: TextView
    private lateinit var tv_liveProfile: TextView
    lateinit var emptyLive: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMyLivePodcast = view.findViewById(R.id.rvMyLivePodcast)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        fullnamePodcastProfile = view.findViewById(R.id.fullnamePodcastProfile)
        timeDateProfile = view.findViewById(R.id.timeDateProfile)
        title_threadProfile = view.findViewById(R.id.title_threadProfile)
        topic_nameProfile = view.findViewById(R.id.topic_nameProfile)
        desc_topicProfile = view.findViewById(R.id.desc_topicProfile)
        imagePostProfile = view.findViewById(R.id.imagePostProfile)
        image_profile_menu_topicProfile = view.findViewById(R.id.image_profile_menu_topicProfile)
        tv_likeProfile = view.findViewById(R.id.tv_likeProfile)
        tv_commentProfile = view.findViewById(R.id.tv_commentProfile)
        tv_shareProfile = view.findViewById(R.id.tv_shareProfile)
        tv_liveProfile = view.findViewById(R.id.tv_liveProfile)

        emptyLive = view.findViewById(R.id.emptyLive)

        imgProfile = view.findViewById(R.id.imgProfile)
        nameProfile = view.findViewById(R.id.nameProfile)
        emailProfile = view.findViewById(R.id.emailProfile)
        logout = view.findViewById(R.id.btnLogout)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        recyclerViewOwn = view.findViewById(R.id.rvProfileOwn)
        recyclerViewOwn.setHasFixedSize(true)
        val layoutManagerOwn =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewOwn.layoutManager = layoutManagerOwn
        mOwn = mutableListOf()
        myOwnTopicsAdapter = MyOwnTopicsAdapter()
        myOwnTopicsAdapter.mContext = requireContext()
        myOwnTopicsAdapter.mOwn = mOwn
        recyclerViewOwn.adapter = myOwnTopicsAdapter

        recyclerViewJoin = view.findViewById(R.id.rvProfileJoin)
        recyclerViewJoin.setHasFixedSize(true)
        val layoutManagerJoin =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewJoin.layoutManager = layoutManagerJoin
        mJoin = mutableListOf()
        myJoinTopicsAdapter = MyJoinTopicsAdapter()
        myJoinTopicsAdapter.mContextJoin = requireContext()
        myJoinTopicsAdapter.mJoin = mJoin
        recyclerViewJoin.adapter = myJoinTopicsAdapter

        getProfile()

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, Login::class.java))
            activity?.finish()
        }

        //jika livepodcast ditekan, maka membuka halaman gotomeet atau end podcast
        rvMyLivePodcast.setOnClickListener {

            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.fl_wrapper, LiveNowDetail(), LiveNowDetail::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }


        //MyOwnTopics
        val ref = FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfUsers")
            .child(firebaseUser.uid)
            .child("ownerOf")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mOwn.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(MyOwnTopicsModel::class.java)
                    mOwn.add(value!!)
                    myOwnTopicsAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //MyJoinTopics
        val reff = FirebaseDatabase.getInstance().reference.child("Topics")
            .child("listOfUsers")
            .child(firebaseUser.uid)
            .child("memberOf")

        reff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mJoin.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(MyOwnTopicsModel::class.java)
                    mJoin.add(value!!)
                    myJoinTopicsAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    interface OnDataReceiveCallback {
        fun onDataReceived(display_name: String?, photo: String?)
    }

    fun getProfile() {
        val reference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                nameProfile.text = value?.fullname.toString()
                emailProfile.text = value?.email.toString()
                email = value?.email.toString()
                myOwnTopicsAdapter.notifyDataSetChanged()

                val imageProfile = storageReference.child("Users/$email/imgProfile")
                imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    imgProfile.setImageBitmap(bitmap)
                }
                readDataLive()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun readDataLive() {
        val referen = FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(firebaseUser.uid)

        referen.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    //check child exist or not
                    val value = snapshot.getValue(LiveNowModel::class.java)
                    nameOfTopicProfile = value!!.topic
                    podcastTitleProfile = value!!.podcastTitle
                    linkMeet = value!!.linkMeet

                } else {
                    nameOfTopicProfile = null
                    podcastTitleProfile = null
                    linkMeet = null
                }

                if (nameOfTopicProfile != null) {
                    rvMyLivePodcast.visibility = View.VISIBLE
                } else {
                    emptyLive.visibility = View.VISIBLE
                }
                myLivePodcast()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun myLivePodcast() {
        if (nameOfTopicProfile != null) {
            //MyLivePodcast
            val refff = FirebaseDatabase.getInstance().reference.child("Topics")
                .child("listOfTopics")
                .child(nameOfTopicProfile!!)
                .child("podcast")
                .child(podcastTitleProfile!!)

//        Toast.makeText(context, "${nameOfTopicProfile} Isi $podcastTitleProfile", Toast.LENGTH_LONG).show()

            refff.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Topic::class.java)
                    fullnamePodcastProfile.text = value?.fullname
                    timeDateProfile.text = value?.timeDate
                    title_threadProfile.text = value?.title
                    topic_nameProfile.text = "t/" + value?.topicName
                    desc_topicProfile.text = value?.description
                    tv_likeProfile.text = value?.like
                    tv_commentProfile.text = value?.comment
                    tv_shareProfile.text = value?.share
//                    tv_liveProfile.text = value?.live

                    val namaTopik = value?.topicName
                    val namaPodcast = value?.title

                    val imageRef =
                        storageReference.child("Topics/$namaTopik/$namaPodcast/imgCreatePodcast")
                    imageRef.getBytes(1024 * 1024).addOnSuccessListener {
                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        imagePostProfile.setImageBitmap(bitmap)
                    }

                    val email = value?.email

                    val imageProfile = storageReference.child("Users/$email/imgProfile")
                    imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        image_profile_menu_topicProfile.setImageBitmap(bitmap)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
}