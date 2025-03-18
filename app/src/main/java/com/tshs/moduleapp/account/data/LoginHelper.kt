package com.tshs.moduleapp.account.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tshs.moduleapp.MainActivity
import com.tshs.moduleapp.R
import com.tshs.moduleapp.ui.Dashboard

class LoginHelper(private val context: Context, private val rootView: View) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun loginUser() {
        val email = rootView.findViewById<EditText>(R.id.username).text.toString().trim()
        val password = rootView.findViewById<EditText>(R.id.password).text.toString().trim()

        val emptyField = rootView.findViewById<TextView>(R.id.emptyField)
        val progressBar = rootView.findViewById<ProgressBar>(R.id.progressBar)

        if(email.isEmpty() || password.isEmpty()){
            emptyField.text = "Please fill in all fields"
            emptyField.visibility = View.VISIBLE
        }

        progressBar.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser
                    if (userId != null) {
                        checkUserVerified(userId, emptyField, progressBar)
                    }
                }else {
                    emptyField.text = "Login Failed: ${task.exception?.message}"
                    emptyField.text = "User ID is null after login."
                    emptyField.visibility = View.VISIBLE
                }
            }
    }

    private fun checkUserVerified(user: FirebaseUser, emptyField: TextView, progressBar: ProgressBar){

        if(user.isEmailVerified){
            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, Dashboard::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }else{
            emptyField.text = "Please verify your email"
            emptyField.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

    }

    fun logoutUser(){
        mAuth.signOut()
        Toast.makeText(context, "Logout Successful!", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

}