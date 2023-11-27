package ru.enlistment.office.ui.screens.user

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.Study
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.view.BaseOutlinedTextField

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

    LaunchedEffect(key1 = Unit, block = {
        if(update) {
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
            }catch (_: Exception){}
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        BaseOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = "Название"
        )

        BaseOutlinedTextField(
            value = currentCourse,
            onValueChange = { currentCourse = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = "Курс"
        )

        BaseOutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = "Начала обучение"
        )

        BaseOutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = "Конец обучение"
        )

        Row(verticalAlignment = Alignment.CenterVertically){
            Text("Отсрочка", fontSize = 22.sp, modifier = Modifier.padding(end = 15.dp))
            Switch(
                checked = postponement,
                onCheckedChange = { postponement= it }
            )
        }

        AnimatedVisibility(visible = name.isNotEmpty() && currentCourse.toIntOrNull() != null && startDate.isNotEmpty() && endDate.isNotEmpty()) {
            Button(onClick = {
                scope.launch {
                    try {
                        val token = userDataStore.getAccessToken()
                        val accounting = Study(name, currentCourse.toInt(), startDate, endDate, postponement)
                        val response = networkApi.userAddStudy(accountId, accounting, "Bearer $token")

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