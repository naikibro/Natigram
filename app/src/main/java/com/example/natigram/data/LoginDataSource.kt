package com.example.natigram.data

import com.example.natigram.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    // Static table of 5 valid users
    private val users = listOf(
        UserCredentials("naikibro@gmail.com", "naiki", LoggedInUser(java.util.UUID.randomUUID().toString(), "Naiki Brotherson")),
        UserCredentials("fabrice@gmail.com", "fabrice", LoggedInUser(java.util.UUID.randomUUID().toString(), "Fabrice Tirolien")),
        UserCredentials("jane@gmail.com", "jane", LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")),
        UserCredentials("john@gmail.com", "john", LoggedInUser(java.util.UUID.randomUUID().toString(), "John Doe")),
        UserCredentials("linus@gmail.com", "linus", LoggedInUser(java.util.UUID.randomUUID().toString(), "Linus torvald"))
    )

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
