package com.example.natigram.data

import com.example.natigram.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.IOException

class LoginDataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private val users = mutableListOf(
            UserCredentials("naikibro@gmail.com", "naiki", LoggedInUser("1", "Naiki Brotherson")),
            UserCredentials("fabrice@gmail.com", "fabrice", LoggedInUser("2", "Fabrice Tirolien")),
            UserCredentials("jane@gmail.com", "jane", LoggedInUser("3", "Jane Doe")),
            UserCredentials("john@gmail.com", "john", LoggedInUser("4", "John Doe")),
            UserCredentials("linus@gmail.com", "linus", LoggedInUser("5", "Linus Torvald"))
        )

        fun getDisplayName(userId: String): String? {
            return users.find { it.loggedInUser.userId == userId }?.loggedInUser?.displayName
        }

        fun addGoogleUser(userId: String, displayName: String) {
            if (users.none { it.loggedInUser.userId == userId }) {
                users.add(UserCredentials("", "", LoggedInUser(userId, displayName)))
            }
        }
    }

    fun login(username: String, password: String, callback: (Result<LoggedInUser>) -> Unit) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    val user = users.find { it.username == username }
                    if (user != null) {
                        callback(Result.Success(user.loggedInUser))
                    } else {
                        val newUser = LoggedInUser(firebaseUser?.uid ?: "", firebaseUser?.displayName ?: "")
                        callback(Result.Success(newUser))
                    }
                } else {
                    callback(Result.Error(IOException("Error logging in", task.exception)))
                }
            }
    }

    fun logout() {
        auth.signOut()
        println("User logged out")
    }

    data class UserCredentials(
        val username: String,
        val password: String,
        val loggedInUser: LoggedInUser
    )
}
