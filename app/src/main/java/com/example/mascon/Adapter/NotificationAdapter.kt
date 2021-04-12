package com.example.mascon.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mascon.Model.NotificationModel
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    lateinit var mContext: Context

    //user di adapter tipe list
    lateinit var mNotif: List<NotificationModel>

    //user di Firebasenya
    lateinit var firebaseUser: FirebaseUser
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var textMeet = ""
    var nameGuest = ""

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.notif_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
//        viewHolder.cbListofUser.visibility = View.VISIBLE
        viewHolder.fullnameNotif.text = mNotif[i].fullname
        viewHolder.dateNotif.text = mNotif[i].dateNotif
        viewHolder.desc_podcast_notif.text = mNotif[i].descPodcast
        viewHolder.podcastTitleNotif.text = mNotif[i].podcastTitle
        viewHolder.topicNameNotif.text = mNotif[i].topicName

        val email = mNotif[i].email
        val imageProfile = storageReference.child("Users/$email/imgProfile")
        imageProfile.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.image_profile_notif.setImageBitmap(bitmap)
        }

        val ownerTopic = mNotif[i].invitedBy
        val linkMeet = mNotif[i].linkMeet
        //cek di database LiveNow, kalo gada link meetnya, brarti nanti nampilin outofdate
        FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(ownerTopic).child("linkMeet").get().addOnSuccessListener {
                if (linkMeet==it.value.toString()){
                    viewHolder.liveTv.visibility = View.VISIBLE
                    viewHolder.live_ic_notif.visibility = View.VISIBLE
                    viewHolder.dateNotif.visibility = View.GONE


                }
            }

//        Glide.with(mContext).load(user.imageurl).into(viewHolder.image_profile)

//        if (user.id == firebaseUser.uid) {
//            viewHolder.cbListofUser.visibility = View.GONE
//        }

        val dataInvited = mutableListOf<String>()
        viewHolder.itemView.setOnClickListener {
            if (viewHolder.liveTv.visibility == View.VISIBLE){
//                viewHolder.progressBar.visibility = View.VISIBLE
//            val livePodcast = LivePodcast()
//            livePodcast.idInviter = mNotif[i].invitedBy
//            livePodcast.idGuest = firebaseUser.uid
//            Log.i("wow", livePodcast.idInviter)
//            Log.i("wow", livePodcast.idGuest)

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(firebaseUser.uid).child("fullname").get().addOnSuccessListener {
                        nameGuest = it.value.toString()
                        val hashMap = HashMap<String, String>()
                        hashMap.put("idGuest", firebaseUser.uid)
                        hashMap.put("nameGuest", nameGuest)
                        val red = FirebaseDatabase.getInstance().reference.child("LiveNow")
                            .child(mNotif[i].invitedBy)
                            .child("listOfInvited")
                            .child(firebaseUser.uid)
                        red.setValue(hashMap)

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                            .child(firebaseUser.uid).child("invited")
                        ref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (snapshot: DataSnapshot in dataSnapshot.children) {
                                    val value = snapshot.getValue(NotificationModel::class.java)
                                    dataInvited.add(value!!.linkMeet)
                                    Log.i("msgg", "datainvited isi : " + dataInvited.toString())

                                    val dataLiveNow = mutableListOf<String>()
                                    val reff = FirebaseDatabase.getInstance().reference.child("LiveNow")
                                    reff.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (snapshot: DataSnapshot in dataSnapshot.children) {
                                                val value = snapshot.getValue(NotificationModel::class.java)
                                                dataLiveNow.add(value!!.linkMeet)

                                                meetNow(dataInvited, dataLiveNow)

//                            viewHolder.progressBar.visibility = View.GONE

                                            }
                                            Log.i("msgg", "datalivenow isi : " + dataLiveNow.toString())
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })

                        Log.i("fairbes", "nameGuest : ${nameGuest}")
                    }.addOnFailureListener {
                        Log.e("fairbes", "Error getting data", it)
                    }

//            ree.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val value = snapshot.getValue(NotificationModel::class.java)
//                    if (value != null) {
//                        val tmp = value.fullname
//                        nameGuest = tmp
//                    }
//                    Log.i("wow", value!!.fullname)
//
//                }
//
//                //                Log.i("wow", value!!.fullname)
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })

                //tiap user yang diundang dan klik itemview di notification fragment, maka nama dan idnya akan disimpan
                //di database LiveNow



//                Handler().postDelayed(
//                    {
//
//                    }, 5000
//                )
            } else {
                Toast.makeText(mContext, "Live podcast out of date", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun meetNow(dataInvited: MutableList<String>, dataLiveNow: MutableList<String>) {
        //for compare
        val listOne: Collection<String> = dataInvited
        val listTwo: Collection<String> = dataLiveNow
        val similar: MutableCollection<String> = java.util.HashSet(listOne)
        similar.retainAll(listTwo)
        Log.i("msgg", "similar isi : " + similar.toString())

        //for split
        val tmp = similar.toString()
        val splitOne = tmp.split("[").toTypedArray()
        val splitTwo = splitOne[1].split("]").toTypedArray()
        textMeet = splitTwo[0].trim()
        Log.i("msgg", "split textmeet isi : " + textMeet.toString())

        if (textMeet == "") {
            Toast.makeText(
                mContext,
                "This podcast is out of date",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom("https://meet.jit.si/$textMeet")
                .setAudioMuted(true)
                .setVideoMuted(true)
                .setAudioOnly(false)
                .setWelcomePageEnabled(false)
                .build()
            JitsiMeetActivity.launch(mContext, options)
        }
    }

    override fun getItemCount(): Int {
        return mNotif.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_profile_notif: CircleImageView = itemView.findViewById(R.id.image_profile_notif)
        var fullnameNotif: TextView = itemView.findViewById(R.id.fullnameNotif)
        var podcastTitleNotif: TextView = itemView.findViewById(R.id.podcastTitleNotif)
        var topicNameNotif: TextView = itemView.findViewById(R.id.topicNameNotif)
        var desc_podcast_notif: TextView = itemView.findViewById(R.id.desc_podcast_notif)
        var dateNotif: TextView = itemView.findViewById(R.id.dateNotif)
        var live_ic_notif: ImageView = itemView.findViewById(R.id.live_ic_notif)
        var liveTv: TextView = itemView.findViewById(R.id.liveTv)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressbar)
    }
}

