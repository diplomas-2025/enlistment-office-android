package ru.enlistment.office.data.network.model.auth

data class AuthRequest(
    val email: String,
    val password: String
)