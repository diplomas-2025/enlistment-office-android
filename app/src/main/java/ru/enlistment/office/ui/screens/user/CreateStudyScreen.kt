package ru.enlistment.office.ui.screens.user

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
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
import ru.enlistment.office.data.network.model.user.Study
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.view.BaseLottieAnimation
import ru.enlistment.office.ui.view.BaseOutlinedTextField
import ru.enlistment.office.ui.view.LottieAnimationType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateStudyScreen(
    accountId: Int,
    update: Boolean,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDataStore = remember { UserDataStore(context) }
    val networkApi = remember { networkApi }

    var name by remember { mutableStateOf("") }
    var currentCourse by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var postponement by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    // Date picker dialog logic
    val showStartDatePicker = remember { mutableStateOf(false) }
    val showEndDatePicker = remember { mutableStateOf(false) }

    val dateSetListener = { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(date.time)

        if (showStartDatePicker.value) {
            startDate = formattedDate
            showStartDatePicker.value = false
        } else if (showEndDatePicker.value) {
            endDate = formattedDate
            showEndDatePicker.value = false
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        if (update) {
            try {
                val token = userDataStore.getAccessToken()
                val response = networkApi.getUserById(accountId, "Bearer $token")

                response.body()?.let { it.accounting?.study?.let {
                    name = it.name
                    currentCourse = it.currentCourse.toString()
                    startDate = it.startDate
                    endDate = it.endDate
                    postponement = it.postponement
                } }
            } catch (_: Exception) {}
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Card for Study Form
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
                    text = "Учёба",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                BaseOutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Название"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Course Input
                BaseOutlinedTextField(
                    value = currentCourse,
                    onValueChange = { currentCourse = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = "Курс"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Start Date Picker
                Button(onClick = { showStartDatePicker.value = true }) {
                    Text("Выбрать дату начала: ${startDate.ifEmpty { "Не выбрано" }}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End Date Picker
                Button(onClick = { showEndDatePicker.value = true }) {
                    Text("Выбрать дату окончания: ${endDate.ifEmpty { "Не выбрано" }}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Postponement Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Отсрочка", fontSize = 22.sp, modifier = Modifier.padding(end = 15.dp))
                    Switch(
                        checked = postponement,
                        onCheckedChange = { postponement = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                AnimatedVisibility(visible = name.isNotEmpty() && currentCourse.toIntOrNull() != null && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                    Button(onClick = {
                        scope.launch {
                            try {
                                val token = userDataStore.getAccessToken()
                                val accounting = Study(name, currentCourse.toInt(), startDate, endDate, postponement)
                                val response = networkApi.userAddStudy(accountId, accounting, "Bearer $token")

                                if (response.isSuccessful)
                                    navController.navigateUp()
                                else
                                    Toast.makeText(context, response.errorBody()?.string().toString(), Toast.LENGTH_SHORT).show()
                            } catch (_: Exception) {}
                        }
                    }) {
                        Text(text = "Сохранить")
                    }
                }
            }
        }
    }

    // Show date picker dialogs
    if (showStartDatePicker.value || showEndDatePicker.value) {
        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}