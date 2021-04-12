package com.example.mascon

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnSignup: Button
    private lateinit var fullnameRegister: EditText
    private lateinit var tvAddImageRegister: TextView
    private lateinit var ivAddImageRegister: ImageView
    private lateinit var tvRemoveImageRegister: TextView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btnSignup = findViewById(R.id.btnSignup)
        fullnameRegister = findViewById(R.id.fullnameRegister)
        ivAddImageRegister = findViewById(R.id.ivAddImageRegister)
        tvAddImageRegister = findViewById(R.id.tvAddImageRegister)
        tvRemoveImageRegister = findViewById(R.id.tvRemoveImageRegister)

        tvRemoveImageRegister.setOnClickListener {
            ivAddImageRegister.setImageURI(null)
            imageURI = null
            ivAddImageRegister.visibility = View.GONE
            tvRemoveImageRegister.visibility = View.GONE
        }

        tvAddImageRegister.setOnClickListener {
            try {
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(3, 3)
                    .setMaxZoom(5)
                    .start(this);
            } catch (e: Exception) {
                Toast.makeText(this, "Errornya adalah " + e, Toast.LENGTH_SHORT).show()
            }
        }

        btnSignup.setOnClickListener {

            if (email.text.toString().trim().isEmpty()) {
                email.error = "Please enter email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (fullnameRegister.text.toString().trim().isEmpty()) {
                fullnameRegister.error = "Please enter fullname"
                fullnameRegister.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString().trim()).matches()) {
                email.error = "Please enter valid email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (password.text.toString().trim().isEmpty()) {
                password.error = "Please enter password"
                password.requestFocus()
                return@setOnClickListener
            }

            if (password.text.toString().trim().length < 6) {
                password.error = "Password less than 6 character"
                password.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(
                email.text.toString().trim(),
                password.text.toString().trim()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val pd = ProgressDialog(this)
                        pd.setTitle("Loading...")
                        pd.setMessage("Please wait..")
                        pd.show()
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser.uid

                        //simpan di database Users
                        val reference = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userId)

                        //simpan di database Topics/listOfUsers
                        val reference1 = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userId)

                        val hashMap = HashMap<String, String>()
                        hashMap.put("id", userId)
                        hashMap.put("email", email.text.toString())
                        hashMap.put("fullname", fullnameRegister.text.toString())

                        if (ivAddImageRegister.visibility != View.GONE) {
                            uploadPicture()
                        }

                        reference1.setValue(hashMap)
                        reference.setValue(hashMap).addOnCompleteListener {
                            if (task.isSuccessful) {
                                val move = Intent(this, Login::class.java)
                                move.putExtra("Kosongan", true)
                                startActivity(move)
                                finish()
                            } else {
                                Toast.makeText(this, "Database failed", Toast.LENGTH_SHORT).show()
                            }
                        }


                    } else {
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageURI = result.uri
                ivAddImageRegister.setImageURI(imageURI)
                ivAddImageRegister.visibility = View.VISIBLE
                tvRemoveImageRegister.visibility = View.VISIBLE
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error pada Crop Image : '$error'", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun uploadPicture() {
        val email = email.text.toString()

        val tmp: StorageReference =
            storageReference.child("Users/$email/imgProfile")
        imageURI?.let { tmp.putFile(it) }
    }
}