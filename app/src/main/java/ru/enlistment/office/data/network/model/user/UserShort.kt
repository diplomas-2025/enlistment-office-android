package ru.enlistment.office.data.network.model.user

data class UserShort(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val email: String,
)