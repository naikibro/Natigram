package com.example.natigram

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.natigram.databinding.ActivityLoginBinding
import com.example.natigram.ui.login.LoginActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure the background of the LinearLayout is an AnimationDrawable
        val linearLayout = findViewById<LinearLayout>(R.id.container)
        val animationDrawable = linearLayout.background as? AnimationDrawable

        animationDrawable?.apply {
            setEnterFadeDuration(2500)
            setExitFadeDuration(5000)
            start()
        }

        auth = Firebase.auth
        //TODO: implement google login
    }

    fun redirectToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
