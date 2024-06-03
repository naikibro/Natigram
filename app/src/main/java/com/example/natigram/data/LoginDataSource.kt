package com.example.natigram.data

import com.example.natigram.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    companion object {
        private val users = listOf(
            UserCredentials("naikibro@gmail.com", "naiki", LoggedInUser("1", "Naiki Brotherson")),
            UserCredentials("fabrice@gmail.com", "fabrice", LoggedInUser("2", "Fabrice Tirolien")),
            UserCredentials("jane@gmail.com", "jane", LoggedInUser("3", "Jane Doe")),
            UserCredentials("john@gmail.com", "john", LoggedInUser("4", "John Doe")),
            UserCredentials("linus@gmail.com", "linus", LoggedInUser("5", "Linus Torvald"))
        )

        fun getDisplayName(userId: String): String? {
            return users.find { it.loggedInUser.userId == userId }?.loggedInUser?.displayName
        }
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val user = users.find { it.username == username && it.password == password }
            if (user != null) {
                Result.Success(user.loggedInUser)
            } else {
                throw IllegalArgumentException("Invalid username or password")
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // For now, we just print a message indicating the user has been logged out.
        // In a real app, you would clear the user's session or token.
        println("User logged out")
    }

    data class UserCredentials(
        val username: String,
        val password: String,
        val loggedInUser: LoggedInUser
    )
}
