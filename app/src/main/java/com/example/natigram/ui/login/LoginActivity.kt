package com.example.natigram.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.natigram.HomeActivity
import com.example.natigram.databinding.ActivityLoginBinding
import com.example.natigram.R

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val autoLogin = binding.autoLogin
        val loginHelper = binding.loginHelper

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            // Ensure the background of the LinearLayout is an AnimationDrawable
            val linearLayout = findViewById<LinearLayout>(R.id.login_container)
            val animationDrawable = linearLayout.background as? AnimationDrawable

            animationDrawable?.apply {
                setEnterFadeDuration(2500)
                setExitFadeDuration(5000)
                start()
            }

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            autoLogin.setOnClickListener {
                username.setText("naikibro@gmail.com")
                password.setText("naiki")
                login.performClick()
            }
        }

        loginHelper.setOnClickListener {
            val intent = Intent(this, LoginBisActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val linearLayout = findViewById<LinearLayout>(R.id.login_container)
            val animationDrawable = linearLayout.background as? AnimationDrawable

            animationDrawable?.apply {
                setEnterFadeDuration(2500)
                setExitFadeDuration(5000)
                start()
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + " " + model.displayName + " !"
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("USER_ID", model.userId)  // Pass user ID
        }
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

// Extension function to simplify setting afterTextChanged listener
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
