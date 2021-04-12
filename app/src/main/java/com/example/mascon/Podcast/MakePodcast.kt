package com.example.mascon.Podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.mascon.Podcast.FragmentMakePodcast.ListofTopicFragment
import com.example.mascon.Podcast.FragmentMakePodcast.ListofUser
import com.example.mascon.Podcast.FragmentMakePodcast.SetPodcastLater
import com.example.mascon.Podcast.FragmentMakePodcast.SetPodcastNow
import com.example.mascon.R
import com.google.android.material.button.MaterialButtonToggleGroup

class MakePodcast : AppCompatActivity() {
    private val listofTopicFragment = ListofTopicFragment()
    private val listofUser = ListofUser()
    private val setPodcastNow = SetPodcastNow()
    private val setPodcastLater = SetPodcastLater()
    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup
    private lateinit var back_makepodcast: ImageView
//    private var pindahFromListofTopicAdapter: String? = ""
//    private var pindahFromSetPodcastNow: Boolean? = false
    //pindah dari setpodcastnow ke imageview invite untuk menampilkan list user
//    private var pindahFromSetPodcastNow_Invite: Boolean? = false
//    private var pindahFromListofUser: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_make_podcast)
        back_makepodcast = findViewById(R.id.back_makepodcast)
        toggleButtonGroup = findViewById(R.id.materialButtonSetPodcast)
        toggleButtonGroup.check(R.id.btnSetPodcastNow)
        val nameOfTopic = intent.getStringExtra("nameOfTopic")
        if (nameOfTopic!=null){
            SetPodcastNow.nameOfTopicText = nameOfTopic
        }
//        pindahFromListofTopicAdapter = intent.getStringExtra("pindahFromListofTopicAdapter")
//        pindahFromSetPodcastNow = intent.getBooleanExtra("pindahFromSetPodcastNow", false)
//        pindahFromSetPodcastNow_Invite =intent.getBooleanExtra("pindahFromSetPodcastNow_Invite", false)
//        pindahFromListofUser = intent.getBooleanExtra("pindahFromListofUser", false)
//        Toast.makeText(this, "INI ISI + $pindahFromListofTopicAdapter",Toast.LENGTH_SHORT).show()

//        if (pindahFromSetPodcastNow as Boolean) {
//            //jika ingin membuka listofTOpic untuk field topic
//            makeCurrentFragment(listofTopicFragment)
//        } else if (pindahFromSetPodcastNow_Invite as Boolean) {
//            //jika ingin membuka listofUser untuk invite
//            makeCurrentFragment(listofUser)
//        } else if (pindahFromListofUser as Boolean) {
//            //jika ingin membuka setPodcastNow setelah memilih user yang diinvite
//            makeCurrentFragment(setPodcastNow)
//        }

        makeCurrentFragment(setPodcastNow)

        back_makepodcast.setOnClickListener {
            SetPodcastNow.nameOfTopicText = null
            finish()
        }


        toggleButtonGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnSetPodcastNow -> {
                        makeCurrentFragment(setPodcastNow)
                    }
                    R.id.btnSetPodcastLater -> {
                        makeCurrentFragment(setPodcastLater)
                    }
                }
            }
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
//        val mBundle = Bundle()
//        mBundle.putString("ValueOfnameoftopic", pindahFromListofTopicAdapter)
//        setPodcastNow.arguments = mBundle


        supportFragmentManager.beginTransaction().apply {
            //if ini untuk berpindah dari SetPodcastNow.kt ketika mengklik TextView topic
            //berpindah ke ListofTopicFragment untuk memilih topic

//            if (fragment == listofTopicFragment) {

            //if dibawah ini untuk memasukkan nilai yang sudah diambil dari ListofTopicFragment
            //nilai nya dipindahkan ke SetPodcastNow.kt di bagian TextView nameOfTopic
//                if (pindahFromListofTopicAdapter != null) {

//                    replace(
//                        R.id.flSetPodcast,
//                        setPodcastNow,
//                        SetPodcastNow::class.java.simpleName
//                    )
//                    addToBackStack(null)
//                    commit()
//                } else {
//                    replace(R.id.flPindahPodcast, fragment)
//                    addToBackStack(null)
//                    commit()
//                    toggleButtonGroup.visibility = View.GONE
//
//                }
//            } else if (fragment == listofUser) {
//                replace(R.id.flPindahPodcast, fragment)
//                addToBackStack(null)
//                commit()
//                toggleButtonGroup.visibility = View.GONE
//            } else

            replace(R.id.flSetPodcast, fragment)
//                addToBackStack(null)
            commit()

        }
    }

    //    jika back button android diklik, maka melakukan aksi
    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.flSetPodcast)
        (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            super.onBackPressed()
        }
    }

    interface IOnBackPressed {
        fun onBackPressed(): Boolean
    }
}