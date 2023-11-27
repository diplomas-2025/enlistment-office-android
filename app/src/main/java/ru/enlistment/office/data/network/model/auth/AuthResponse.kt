package ru.enlistment.office.data.network.model.auth

import ru.enlistment.office.data.network.model.user.UserRole

data class AuthResponse(
    val id: Int,
    val accessToken: String,
    val roles: List<UserRole>
)