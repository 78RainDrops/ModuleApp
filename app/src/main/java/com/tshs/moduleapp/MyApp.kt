package com.tshs.moduleapp

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MyApp: Application() {
    companion object{
        lateinit var mAuth: FirebaseAuth
    }

    override fun onCreate() {
        super.onCreate()
        mAuth = FirebaseAuth.getInstance()
    }
}