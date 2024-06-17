package com.example.natigram.data

import com.example.natigram.data.model.LoggedInUser

class LoginRepository(val dataSource: LoginDataSource) {

    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String, callback: (Result<LoggedInUser>) -> Unit) {
        dataSource.login(username, password) { result ->
            if (result is Result.Success) {
                setLoggedInUser(result.data)
            }
            callback(result)
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}
