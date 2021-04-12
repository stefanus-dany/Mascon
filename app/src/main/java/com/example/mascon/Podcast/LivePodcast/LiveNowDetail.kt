package com.example.mascon.Podcast.LivePodcast

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mascon.Fragment.HomeFragment
import com.example.mascon.Fragment.ProfileFragment
import com.example.mascon.MainActivity
import com.example.mascon.Model.LiveNowModel
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class LiveNowDetail : Fragment() {
    private lateinit var endMeet : Button
    private lateinit var goToMeet : Button
    private lateinit var back_livenowdetail : ImageView
    private lateinit var firebaseUser : FirebaseUser
    var linkMeet : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.live_now_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        endMeet = view.findViewById(R.id.endMeet)
        back_livenowdetail = view.findViewById(R.id.back_livenowdetail)
        goToMeet = view.findViewById(R.id.goToMeet)

        FirebaseDatabase.getInstance().reference.child("LiveNow")
            .child(firebaseUser.uid).child("linkMeet").get().addOnSuccessListener {
                linkMeet = it.value.toString()
            }
//        reff.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val value = snapshot.getValue(LiveNowModel::class.java)
//                linkMeet = value!!.linkMeet
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })

        back_livenowdetail.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        goToMeet.setOnClickListener {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom("https://meet.jit.si/$linkMeet")
                .setAudioMuted(true)
                .setVideoMuted(true)
                .setAudioOnly(false)
                .setWelcomePageEnabled(false)
                .build()
            JitsiMeetActivity.launch(context, options)
        }

        endMeet.setOnClickListener {
            Toast.makeText(context, "Podcast Live has been deleted", Toast.LENGTH_LONG).show()
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.fl_wrapper, ProfileFragment(), ProfileFragment::class.java.simpleName)
                commit()
            }
                FirebaseDatabase.getInstance().reference.child("LiveNow")
                    .child(firebaseUser.uid).removeValue()
            }
        }
    }