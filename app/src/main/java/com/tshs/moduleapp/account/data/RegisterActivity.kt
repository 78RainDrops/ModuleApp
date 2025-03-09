package com.tshs.moduleapp.account.data

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tshs.moduleapp.R
import com.tshs.moduleapp.databinding.ActivityRegisterBinding

class RegisterActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstaceState: Bundle?){
        super.onCreate(savedInstaceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.regpassword.addTextChangedListener(passwordWatcher)

        binding.registerbtn.setOnClickListener{
            registerUser()
        }
        // Back to login button
        binding.button3.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val fullName = binding.regfullname.text.toString().trim()
        val grade = binding.reggrade.text.toString().trim()
        val strand = binding.regstrand.text.toString().trim()
        val email = binding.regemail.text.toString().trim()
        val password = binding.regpassword.text.toString().trim()
        val confirmPassword = binding.regconfirmpassword.text.toString().trim()

        // Validation
        if (fullName.isEmpty() || grade.isEmpty() || strand.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            binding.strongPassword.text = "Please fill in all fields"
            binding.strongPassword.visibility = View.VISIBLE
            return
        }

        if (password != confirmPassword) {
            binding.strongPassword.text = "Passwords do not match"
            binding.strongPassword.visibility = View.VISIBLE
            return
        }

        if (checkPasswordStrength(password) < 3) { // Require at least a "Strong" password
            binding.strongPassword.text = "Password is too weak"
            binding.strongPassword.visibility = View.VISIBLE
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        // Register user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser!!.uid
                    saveUserData(userId, fullName, grade, strand, email)
                } else {
                    binding.strongPassword.text = "Registration Failed: ${task.exception?.message}"
                    binding.strongPassword.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }
    }

    private fun saveUserData(userId: String, fullName: String, grade: String, strand: String, email: String) {
        val user = hashMapOf(
            "fullname" to fullName,
            "grade" to grade,
            "strand" to strand,
            "email" to email
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                setResult(RESULT_OK)
                finish() // Close RegisterActivity
            }
            .addOnFailureListener {
                binding.strongPassword.text = "Failed to save user data"
                binding.strongPassword.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val password = s.toString()
            val strength = checkPasswordStrength(password)

            if (password.isEmpty()) {
                binding.strongPassword.visibility = View.GONE
                return
            }

            binding.strongPassword.visibility = View.VISIBLE

            when (strength) {
                0 -> {
                    binding.strongPassword.text = "Password too short"
                    binding.strongPassword.setTextColor(resources.getColor(R.color.red))
                }
                1 -> {
                    binding.strongPassword.text = "Weak password"
                    binding.strongPassword.setTextColor(resources.getColor(R.color.red))
                }
                2 -> {
                    binding.strongPassword.text = "Moderate password"
                    binding.strongPassword.setTextColor(resources.getColor(R.color.orange))
                }
                3 -> {
                    binding.strongPassword.text = "Strong password"
                    binding.strongPassword.setTextColor(resources.getColor(R.color.green))
                }
                4 -> {
                    binding.strongPassword.text = "Very Strong password"
                    binding.strongPassword.setTextColor(resources.getColor(R.color.green))
                }
            }
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