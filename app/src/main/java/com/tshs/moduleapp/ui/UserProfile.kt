package com.tshs.moduleapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tshs.moduleapp.MainActivity
import com.tshs.moduleapp.R

class UserProfile : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var userInfoTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar2: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true);
//        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar2 = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar2)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(null)

        val user = FirebaseAuth.getInstance().currentUser
        userInfoTextView = findViewById<TextView>(R.id.userInfoTextView)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        if(user != null){
            userInfoTextView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            fetchUserName(user.uid)
        }else{
            userInfoTextView.text = "No user Logged in"
        }


//        val logoutButton = findViewById<Button>(R.id.logoutButton)
//        logoutButton.setOnClickListener {
//            logoutUser()
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchUserName(userId: String){
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("fullname") ?: "User"
                    userInfoTextView.text = "Welcome, ${fullName}"
                } else {
                    userInfoTextView.text = "User data not found"
                }
            }
            .addOnFailureListener {
                userInfoTextView.text = "Failed to retrieve user info"
            }
            .addOnCompleteListener {
                userInfoTextView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        finish() // Closes activity when back button is pressed
//        return true
//    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Navigate back to previous screen
        return true
    }
    fun logoutUser(view: View) {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Close dashboard after logout
    }
}