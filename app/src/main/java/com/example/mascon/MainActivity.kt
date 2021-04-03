package com.example.mascon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.mascon.Fragment.*
import com.example.mascon.Podcast.MakePodcast
import com.example.mascon.Topics.MakeTopics
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var logout: Button
    private lateinit var pindah: Button
    private lateinit var bottomNavigation : BottomNavigationView
    var saveIcon : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        pindah = findViewById(R.id.pindah)
        pindah.setOnClickListener {
            startActivity(Intent(this, MakePodcast::class.java))
        }
        logout = findViewById(R.id.btnLogout)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        bottomNavigation = findViewById(R.id.bottom_navigation)
        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val addFragment = AddFragment()
        val notificationFragment = NotificationFragment()
        val profileFragment = ProfileFragment()

        makeCurrentFragment(homeFragment, R.id.home)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home ->makeCurrentFragment(homeFragment, R.id.home)
                R.id.search -> makeCurrentFragment(searchFragment, R.id.search)
                R.id.addBox ->{
                    val bottomSheetDialog = BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog)
                    var bottomSheetView : View = LayoutInflater.from(this).inflate(R.layout.fragment_add, findViewById(R.id.fragmentAddID))
                    if (this!=null){
                        val parentViewGroup = parent as ViewGroup?
                        parentViewGroup?.removeAllViews();
                    }
                    bottomSheetDialog.setContentView(bottomSheetView)
                    bottomSheetDialog.show()

                    bottomSheetView.findViewById<TextView>(R.id.tvMakeTopic).setOnClickListener {
                        startActivity(Intent(this, MakeTopics::class.java))
                    }


                    bottomSheetView.findViewById<TextView>(R.id.tvMakePodcast)


//                    if (saveIcon==R.id.home){
//                        makeCurrentFragment(homeFragment, saveIcon)
//                    } else if(saveIcon==R.id.search){
//                        makeCurrentFragment(searchFragment, saveIcon)
//                    } else if(saveIcon==R.id.notification){
//                        makeCurrentFragment(notificationFragment, saveIcon)
//                    } else if(saveIcon==R.id.profile){
//                        makeCurrentFragment(profileFragment, saveIcon)
//                    }
                }


                R.id.notification -> makeCurrentFragment(notificationFragment, R.id.notification)
                R.id.profile -> makeCurrentFragment(profileFragment, R.id.profile)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment : Fragment, item: Int){
        supportFragmentManager.beginTransaction().apply {
            saveIcon = item
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }

//    private fun notMove(fragment : Fragment){
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.fl_wrapper, fragment)
//            commit()
//        }
//    }
}