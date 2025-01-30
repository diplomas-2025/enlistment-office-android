package ru.enlistment.office.data.network.model.auth

import ru.enlistment.office.data.network.model.user.UserRole

data class AuthResponse(
    val userId: Int,
    val accessToken: String,
    val roles: List<UserRole>
)