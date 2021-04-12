package com.example.mascon

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var email : EditText? = null
    private lateinit var password : EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignup = findViewById(R.id.account)


        btnSignup.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        btnLogin.setOnClickListener {

            if(email?.text.toString().trim().isEmpty()){
                email?.error = "Please enter email"
                email?.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email?.text.toString().trim()).matches()){
                email?.error = "Please enter valid email"
                email?.requestFocus()
                return@setOnClickListener
            }

            if(password.text.toString().trim().isEmpty()){
                password.error = "Please enter password"
                password.requestFocus()
                return@setOnClickListener
            }
            val pd = ProgressDialog(this)
            pd.setTitle("Loading...")
            pd.setMessage("Please wait..")
            pd.show()

            auth.signInWithEmailAndPassword(email?.text.toString().trim(), password.text.toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.currentUser.uid)
                        reference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot){
                                pd.dismiss()
                                val user = auth.currentUser
                                updateUI(user)
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                pd.dismiss()
                            }
                        })



                    } else {
                        // If sign in fails, display a message to the user.
                        updateUI(null)
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        var a = intent.getBooleanExtra("Kosongan", false)
        if (a==true){
            updateUI(null)
        }
        else{
            val currentUser : FirebaseUser? = auth.currentUser
            updateUI(currentUser)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?){
        if (currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}