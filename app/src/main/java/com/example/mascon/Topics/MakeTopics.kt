package com.example.mascon.Topics

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.mascon.MainActivity
import com.example.mascon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MakeTopics : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var etTopic: EditText
    private lateinit var etDescriptionTopic: EditText
    private lateinit var btnDone: Button
    private lateinit var back_maketopics: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_make_topics)
        auth = FirebaseAuth.getInstance()
        etTopic = findViewById(R.id.etTopic)
        etDescriptionTopic = findViewById(R.id.etDescriptionTopic)
        btnDone = findViewById(R.id.btnDone)
        back_maketopics = findViewById(R.id.back_maketopics)

        back_maketopics.setOnClickListener {
            finish()
        }

        btnDone.setOnClickListener {

            if (etTopic.text.toString().trim().isEmpty()) {
                etTopic.error = "Please enter the topic"
                etTopic.requestFocus()
                return@setOnClickListener
            }

            if (etDescriptionTopic.text.toString().trim().isEmpty()) {
                etDescriptionTopic.error = "Please enter description of the topic"
                etDescriptionTopic.requestFocus()
                return@setOnClickListener
            }

            val pd = ProgressDialog(this)
            pd.setTitle("Loading...")
            pd.setMessage("Please wait..")
            pd.show()

            FirebaseDatabase.getInstance().reference.child("Topics").child("listOfTopics")
                .child(etTopic.text.toString())
                .get().addOnSuccessListener {
                    Log.i("cekMakeS", it.value.toString())
                    if (it.value == null) {
                        Log.i("cekMake", it.value.toString())
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser.uid
                        val topik =
                            FirebaseDatabase.getInstance().reference.child("Topics")
                                .child("listOfUsers")
                                .child(userId)
                                .child("ownerOf")
                                .child(etTopic.text.toString())

                        val reference =
                            FirebaseDatabase.getInstance().reference.child("Topics")
                                .child("listOfTopics")
                                .child(etTopic.text.toString())

                        val ownerTopic =
                            FirebaseDatabase.getInstance().reference.child("Topics")
                                .child("listOfTopics")
                                .child(etTopic.text.toString()).child("ownerTopic")

                        val hashMap = HashMap<String, String>()
                        hashMap.put("topic", etTopic.text.toString())
                        hashMap.put("description", etDescriptionTopic.text.toString())
                        hashMap.put("memberCount", 1.toString())

                        topik.setValue(hashMap)
                        reference.setValue(hashMap).addOnCompleteListener {
                            if (it.isSuccessful) {
                                ownerTopic.setValue(userId)
                                val intent = Intent(this, MenuTopic::class.java)
                                intent.putExtra("topicName", etTopic.text.toString())
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Adding Topic is failed",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            pd.dismiss()
                        }
                    } else {
                        pd.dismiss()
                        Toast.makeText(this, "This topic already exists", Toast.LENGTH_SHORT)
                            .show()
                    }

                }


        }
    }
}