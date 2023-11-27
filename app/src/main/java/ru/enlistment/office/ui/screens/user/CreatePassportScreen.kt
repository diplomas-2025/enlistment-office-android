package ru.enlistment.office.ui.screens.user

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.Gender
import ru.enlistment.office.data.network.model.user.Passport
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.view.BaseOutlinedTextField

@Composable
fun CreatePassportScreen(
    accountId: Int,
    update: Boolean,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDataStore = remember { UserDataStore(context) }
    val networkApi = remember { networkApi }

    var issued by remember { mutableStateOf("") }
    var dateIssue by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var first_name by remember { mutableStateOf("") }
    var last_name by remember { mutableStateOf("") }
    var middle_name by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var addressCity by remember { mutableStateOf("") }
    var addressStreet by remember { mutableStateOf("") }
    var addressHouseNumber by remember { mutableStateOf("") }
    var addressApartmentNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.M) }

    LaunchedEffect(key1 = Unit, block = {
        if(update) {
            try {
                val token = userDataStore.getAccessToken()

                val response = networkApi.getUserById(accountId, "Bearer $token")

                response.body()?.let { it.accounting?.passport?.let {
                    issued = it.issued
                    dateIssue = it.dateIssue
                    series = it.series
                    number = it.number
                    first_name = it.first_name
                    last_name = it.last_name
                    middle_name = it.middle_name
                    birthday = it.birthday
                    addressCity = it.addressCity
                    addressStreet = it.addressStreet
                    addressHouseNumber = it.addressHouseNumber
                    addressApartmentNumber = it.addressApartmentNumber.toString()
                    gender = it.gender
                } }
            }catch (_: Exception){}
        }
    })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        item {
            BaseOutlinedTextField(
                value = issued,
                onValueChange = { issued = it },
                label = "Выдан"
            )

            BaseOutlinedTextField(
                value = dateIssue,
                onValueChange = { dateIssue = it },
                label = "Дата выдачи"
            )

            BaseOutlinedTextField(
                value = series,
                onValueChange = { series = it },
                label = "Серия"
            )

            BaseOutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = "Номер"
            )

            BaseOutlinedTextField(
                value = first_name,
                onValueChange = { first_name = it },
                label = "Имя"
            )

            BaseOutlinedTextField(
                value = last_name,
                onValueChange = { last_name = it },
                label = "Фамилия"
            )

            BaseOutlinedTextField(
                value = middle_name,
                onValueChange = { middle_name = it },
                label = "Отчество"
            )

            BaseOutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                label = "День рождения"
            )

            BaseOutlinedTextField(
                value = addressCity,
                onValueChange = { addressCity = it },
                label = "Город"
            )

            BaseOutlinedTextField(
                value = addressStreet,
                onValueChange = { addressStreet = it },
                label = "Улица"
            )

            BaseOutlinedTextField(
                value = addressHouseNumber,
                onValueChange = { addressHouseNumber = it },
                label = "Дом"
            )

            BaseOutlinedTextField(
                value = addressApartmentNumber,
                onValueChange = { addressApartmentNumber = it },
                label = "Квартира",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AnimatedVisibility(
                visible = issued.isNotEmpty() && dateIssue.isNotEmpty() && series.isNotEmpty() && number.isNotEmpty()
                        && first_name.isNotEmpty() && last_name.isNotEmpty() && middle_name.isNotEmpty()
                        && birthday.isNotEmpty() && addressCity.isNotEmpty() && addressStreet.isNotEmpty()
                        && addressHouseNumber.isNotEmpty() && addressApartmentNumber.toIntOrNull() != null
            ) {
                Button(onClick = {
                    scope.launch {
                        try {
                            val token = userDataStore.getAccessToken()
                            val accounting = Passport(
                                issued = issued,
                                dateIssue = dateIssue,
                                series = series,
                                number = number,
                                first_name = first_name,
                                last_name = last_name,
                                middle_name = middle_name,
                                birthday = birthday,
                                addressCity =  addressCity,
                                addressStreet = addressStreet,
                                addressHouseNumber = addressHouseNumber,
                                addressApartmentNumber = addressApartmentNumber.toInt(),
                                gender = gender
                            )
                            val response = networkApi.userAddPassport(accountId, accounting, "Bearer $token")

                            if(response.isSuccessful)
                                navController.navigateUp()
                            else
                                Toast.makeText(context, response.errorBody()?.string().toString(), Toast.LENGTH_SHORT).show()
                        }catch (_: Exception) {}
                    }
                }) {
                    Text(text = "Сохранить")
                }
            }
        }
    }
}