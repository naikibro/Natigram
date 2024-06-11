package com.example.natigram.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.natigram.HomeActivity
import com.example.natigram.R
import com.example.natigram.data.LoginDataSource
import com.example.natigram.databinding.ActivityLoginBisBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginBisActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var binding: ActivityLoginBisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        oneTapClient = Identity.getSignInClient(this)

        binding.signInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                signInLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error during Google Sign-In", e)
            }
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithCredential:success")
                            val user = auth.currentUser
                            user?.let {
                                LoginDataSource.addGoogleUser(it.uid, it.displayName ?: "")
                                updateUiWithUser(it)
                                navigateToHomeActivity(it)
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                        }
                    }
            } else {
                Log.d(TAG, "No ID token!")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sign-in credential", e)
        }
    }

    private fun updateUiWithUser(user: FirebaseUser) {
        val welcome = getString(R.string.welcome) + " " + user.displayName + "!"
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun navigateToHomeActivity(user: FirebaseUser) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("USER_ID", user.uid)  // Pass user ID
        }
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "LoginBisActivity"
    }
}
