package com.tshs.moduleapp.account.data

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tshs.moduleapp.R

class RegisterHelper(private val context: Context, private val rootView: View) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(){
        val fullname  = rootView.findViewById<EditText>(R.id.regfullname).text.toString().trim()
        val grade = rootView.findViewById<EditText>(R.id.reggrade).text.toString().trim()
        val strand = rootView.findViewById<EditText>(R.id.regstrand).text.toString().trim()
        val email = rootView.findViewById<EditText>(R.id.regemail).text.toString().trim()
        val password = rootView.findViewById<EditText>(R.id.regpassword).text.toString().trim()
        val confirmpassword = rootView.findViewById<EditText>(R.id.regconfirmpassword).text.toString().trim()

        val strongPasswordTextView = rootView.findViewById<TextView>(R.id.strong_password)
        val progressBar = rootView.findViewById<ProgressBar>(R.id.progressBar)

        if(fullname.isEmpty() || grade.isEmpty() || strand.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()){
            strongPasswordTextView.text = "Please fill in all fields"
            strongPasswordTextView.visibility = View.VISIBLE
            return
        }

        if(password != confirmpassword){
            strongPasswordTextView.text = "Passwords do not match"
            strongPasswordTextView.visibility = View.VISIBLE
            return
        }

        if(checkPasswordStrength(password) < 3){ // Require at least a "Strong" password
            strongPasswordTextView.text = "Password is too weak"
            strongPasswordTextView.visibility = View.VISIBLE
            return
        }

        progressBar.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    if (userId != null) {
//                        val userId = mAuth.currentUser!!.uid
                        Log.d("RegisterHelper", "User registered successfully: $userId")
                        saveUserData(userId, fullname, grade, strand, email, strongPasswordTextView, progressBar)
                    } else {
                        Log.e("RegisterHelper", "User ID is null after registration.")
                    }
                }else{
                    Log.e("RegisterHelper", "Registration failed: ${task.exception?.message}")
                    strongPasswordTextView.text = "Registration Failed: ${task.exception?.message}"
                    strongPasswordTextView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun saveUserData(
        userId: String,
        fullname: String,
        grade: String,
        strand: String,
        email: String,
        strongPasswordTextView: TextView,
        progressBar: ProgressBar
    ){
        val user = hashMapOf(
            "fullname" to fullname,
            "grade" to grade,
            "strand" to strand,
            "email" to email
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(context, "Registration Successfull!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE

                val loginForm = rootView.findViewById<View>(R.id.login_form)
                val createAccountForm = rootView.findViewById<View>(R.id.create_account_form)

                loginForm.visibility = View.VISIBLE
                createAccountForm.visibility = View.GONE
            }
            .addOnFailureListener {
                strongPasswordTextView.text = "Failed to save user data"
                strongPasswordTextView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
    }


    // Function to check password strength
    private fun checkPasswordStrength(password: String): Int {
        var score = 0
        var hasUpper = false
        var hasLower = false
        var hasDigit = false
        var hasSpecial = false
        val specialCharacters = "!@#$%^&*()-_=+[]{}|;:,.<>?/"

        if (password.length >= 8) score += 1
        for (char in password) {
            when {
                char.isUpperCase() -> hasUpper = true
                char.isLowerCase() -> hasLower = true
                char.isDigit() -> hasDigit = true
                specialCharacters.contains(char) -> hasSpecial = true
            }
        }
        if (hasUpper) score += 1
        if (hasLower) score += 1
        if (hasDigit) score += 1
        if (hasSpecial) score += 1

        return score.coerceAtMost(4) // Max score of 4
    }
}