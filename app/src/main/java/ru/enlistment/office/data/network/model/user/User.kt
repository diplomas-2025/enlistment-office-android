package ru.enlistment.office.data.network.model.user

data class User(
    val id: Int,
    val email: String,
    val roles: List<UserRole>,
    val accounting: Accounting?
)

data class Accounting(
    val status: UserAccountingStatus,
    val maritalStatus: UserAccountingMaritalStatus,
    val phoneNumber: String,
    val phoneNumberCode: Int,
    val enlistmentOffice: EnlistmentOffice,
    val work: Work?,
    val study: Study?,
    val passport: Passport?,
    val enlistmentSummonDocuments: List<EnlistmentSummonDocument>
)

data class CreateAccounting(
    val status: UserAccountingStatus,
    val maritalStatus: UserAccountingMaritalStatus,
    val phoneNumber: String,
    val phoneNumberCode: Int,
    val enlistmentOfficeId: Int
)

enum class UserAccountingMaritalStatus(val title: String) {
    MARRIED("В браке"),
    NOT_MARRIED("Не женат")
}

enum class UserAccountingStatus(val title: String) {
    FIT("Годен"),
    NO_FIT("Не годен")
}

data class EnlistmentOffice(
    val id: Int,
    val city: String,
    val street: String,
    val houseNumber: String
)

data class Work(
    val organization: String,
    val position: String,
    val postponement: Boolean
)

data class Study(
    val name: String,
    val currentCourse: Int,
    val startDate: String,
    val endDate: String,
    val postponement: Boolean
)

data class Passport(
    val issued: String,
    val dateIssue: String,
    val series: String,
    val number: String,
    val first_name: String,
    val last_name: String,
    val middle_name: String,
    val gender: Gender,
    val birthday: String,
    val addressCity: String,
    val addressStreet: String,
    val addressHouseNumber: String,
    val addressApartmentNumber: Int
)

enum class Gender {
    M,
    W
}

data class EnlistmentSummonDocument(
    val id: Int,
    val type: EnlistmentSummonDocumentType,
    val sendDate: String
)

enum class EnlistmentSummonDocumentType(val title: String) {
    ARMY("Армия"),
    MEDICAL_COMMISSION("Медкомиссия")
}