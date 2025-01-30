package ru.enlistment.office.ui.screens.user

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.Gender
import ru.enlistment.office.data.network.model.user.Passport
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.view.BaseLottieAnimation
import ru.enlistment.office.ui.view.BaseOutlinedTextField
import ru.enlistment.office.ui.view.LottieAnimationType
import java.text.SimpleDateFormat
import java.util.*

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

    // For date picker dialogs
    val calendar = Calendar.getInstance()
    val showDateIssuePicker = remember { mutableStateOf(false) }
    val showBirthdayPicker = remember { mutableStateOf(false) }

    val dateSetListener = { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(date.time)

        if (showDateIssuePicker.value) {
            dateIssue = formattedDate
            showDateIssuePicker.value = false
        } else if (showBirthdayPicker.value) {
            birthday = formattedDate
            showBirthdayPicker.value = false
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        if (update) {
            try {
                val token = userDataStore.getAccessToken()

                val response = networkApi.getUserById(accountId, "Bearer $token")

                response.body()?.let {
                    it.accounting?.passport?.let {
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
                    }
                }
            } catch (_: Exception) {
            }
        }
    })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            // Card for Passport Form
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BaseLottieAnimation(
                        type = LottieAnimationType.Registration,
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Паспорт",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Passport Fields
                    BaseOutlinedTextField(
                        value = issued,
                        onValueChange = { issued = it },
                        label = "Выдан"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date Issue Picker Button
                    Button(onClick = { showDateIssuePicker.value = true }) {
                        Text("Дата выдачи: ${dateIssue.ifEmpty { "Не выбрано" }}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = series,
                        onValueChange = { series = it },
                        label = "Серия"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = "Номер"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = first_name,
                        onValueChange = { first_name = it },
                        label = "Имя"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = last_name,
                        onValueChange = { last_name = it },
                        label = "Фамилия"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = middle_name,
                        onValueChange = { middle_name = it },
                        label = "Отчество"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Birthday Picker Button
                    Button(onClick = { showBirthdayPicker.value = true }) {
                        Text("Дата рождения: ${birthday.ifEmpty { "Не выбрано" }}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = addressCity,
                        onValueChange = { addressCity = it },
                        label = "Город"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = addressStreet,
                        onValueChange = { addressStreet = it },
                        label = "Улица"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = addressHouseNumber,
                        onValueChange = { addressHouseNumber = it },
                        label = "Дом"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BaseOutlinedTextField(
                        value = addressApartmentNumber,
                        onValueChange = { addressApartmentNumber = it },
                        label = "Квартира",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
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
                                        addressCity = addressCity,
                                        addressStreet = addressStreet,
                                        addressHouseNumber = addressHouseNumber,
                                        addressApartmentNumber = addressApartmentNumber.toInt(),
                                        gender = gender
                                    )
                                    val response = networkApi.userAddPassport(
                                        accountId,
                                        accounting,
                                        "Bearer $token"
                                    )

                                    if (response.isSuccessful)
                                        navController.navigateUp()
                                    else
                                        Toast.makeText(
                                            context,
                                            response.errorBody()?.string().toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                } catch (_: Exception) {
                                }
                            }
                        }) {
                            Text(text = "Сохранить")
                        }
                    }
                }
            }
        }
    }

    // Show date picker dialogs
    if (showDateIssuePicker.value || showBirthdayPicker.value) {
        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}