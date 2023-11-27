package ru.enlistment.office.data.database

import android.content.Context
import ru.enlistment.office.data.network.model.auth.AuthResponse
import ru.enlistment.office.data.network.model.user.UserRole

class UserDataStore(context: Context) {

    private val shared = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    fun getAccessToken(): String? {
        return try {
            shared.getString("access_token", null)
        }catch (e: Exception) {
            null
        }
    }

    fun getIsAdmin(): Boolean {
        return try {
            shared.getBoolean("is_admin", false)
        }catch (e: Exception) {
            false
        }
    }

    fun save(authResponse: AuthResponse) {
        shared.edit()
            .putString("access_token", authResponse.accessToken)
            .putInt("user_id", authResponse.id)
            .putBoolean("is_admin", isAdmin(authResponse.roles))
            .apply()
    }

    private fun isAdmin(r: List<UserRole>): Boolean {
        return r.contains(UserRole.ADMIN)
    }
}