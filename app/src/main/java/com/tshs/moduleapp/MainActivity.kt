package com.tshs.moduleapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tshs.moduleapp.account.data.LoginHelper
import com.tshs.moduleapp.account.data.RegisterHelper
import com.tshs.moduleapp.databinding.AccountBinding
import com.tshs.moduleapp.ui.Dashboard
import com.tshs.moduleapp.ui.UserProfile

class MainActivity: AppCompatActivity() {

    private lateinit var binding: AccountBinding
    private lateinit var registerHelper: RegisterHelper
    private lateinit var loginHelper: LoginHelper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContentView(R.layout.account)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        binding = AccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerHelper = RegisterHelper(this, binding.root)
        loginHelper = LoginHelper(this, binding.root)


        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        //    find views
        val loginForm = findViewById<View>(R.id.login_form)
        val createAccountForm = findViewById<View>(R.id.create_account_form)
        val signinButton = createAccountForm.findViewById<View>(R.id.button3)
        val createAccountButton = loginForm.findViewById<View>(R.id.button2)
        val registerButton = createAccountForm.findViewById<View>(R.id.registerbtn)
        val loginButton = loginForm.findViewById<View>(R.id.login)

        //    set initial visibility
        loginForm.visibility = View.VISIBLE
        createAccountForm.visibility = View.GONE
        createAccountButton.setOnClickListener{
            loginForm.visibility = View.GONE
            createAccountForm.visibility = View.VISIBLE
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivityForResult(intent, 1)
        }
        signinButton.setOnClickListener{
            loginForm.visibility = View.VISIBLE
            createAccountForm.visibility = View.GONE
        }
        //register user
        registerButton.setOnClickListener{
            registerHelper.registerUser()
        }

        loginButton.setOnClickListener{
            loginHelper.loginUser()
        }

//

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==1 && resultCode == RESULT_OK){
            val loginForm = findViewById<View>(R.id.login_form)
            val createAccountForm = findViewById<View>(R.id.create_account_form)

            loginForm.visibility = View.VISIBLE
            createAccountForm.visibility = View.GONE

        }
    }

    fun checkPasswordStrength(password: String): Int {
        var score = 0
        var hasUpper = false
        var hasLower = false
        var hasDigit = false
        var hasSpecial = false
        val specialCharacters = "!@#$%^&*()-_=+[]{}|;:,.<>?"

        if (password.length >= 8) {
            score += 1
        } else {
            return 0
        }
        // Check character types
        for (char in password) {
            when {
                char.isUpperCase() -> hasUpper = true
                char.isLowerCase() -> hasLower = true
                char.isDigit() -> hasDigit = true
                specialCharacters.contains(char) -> hasSpecial = true
            }
        }

        // Increase score based on character diversity
        if (hasUpper) score += 1
        if (hasLower) score += 1
        if (hasDigit) score += 1
        if (hasSpecial) score += 1

        return score
    }

}