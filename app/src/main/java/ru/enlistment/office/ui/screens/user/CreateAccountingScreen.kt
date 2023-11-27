package ru.enlistment.office.ui.screens.user

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.CreateAccounting
import ru.enlistment.office.data.network.model.user.EnlistmentOffice
import ru.enlistment.office.data.network.model.user.UserAccountingMaritalStatus
import ru.enlistment.office.data.network.model.user.UserAccountingStatus
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.theme.tintColor
import ru.enlistment.office.ui.view.BaseOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountingScreen(
    userId: Int,
    update: Boolean,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDataStore = remember { UserDataStore(context) }
    val networkApi = remember { networkApi }
    var enlistmentOffices by remember { mutableStateOf(emptyList<EnlistmentOffice>()) }
    var enlistmentOfficeId by remember { mutableStateOf<Int?>(null) }
    var status by remember { mutableStateOf(UserAccountingStatus.FIT) }
    var maritalStatus by remember { mutableStateOf(UserAccountingMaritalStatus.MARRIED) }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneNumberCode by remember { mutableStateOf("7") }

    LaunchedEffect(key1 = Unit, block = {
        if(update) {
            try {
                val token = userDataStore.getAccessToken()

                val response = networkApi.getUserById(userId, "Bearer $token")

                response.body()?.let { it.accounting?.let {
                    enlistmentOfficeId = it.enlistmentOffice.id
                    status = it.status
                    maritalStatus = it.maritalStatus
                    phoneNumber = it.phoneNumber
                    phoneNumberCode = it.phoneNumberCode.toString()
                } }
            }catch (_: Exception){}
        }

        networkApi.getAllEnlistmentOffices().body()?.let { enlistmentOffices = it }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        LazyRow {
            items(enlistmentOffices) {
                Card(
                    modifier = Modifier.padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                    border = if(enlistmentOfficeId == it.id) BorderStroke(2.dp, tintColor) else null,
                    onClick = { enlistmentOfficeId = it.id }
                ) {
                    Text(
                        text = "г. ${it.city} ул. ${it.street}, ${it.houseNumber}",
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }
        }

        LazyRow {
            items(UserAccountingStatus.values()) {
                Card(
                    modifier = Modifier.padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                    border = if(status == it) BorderStroke(2.dp, tintColor) else null,
                    onClick = { status = it }
                ) {
                    Text(text = it.title, modifier = Modifier.padding(15.dp))
                }
            }
        }

        LazyRow {
            items(UserAccountingMaritalStatus.values()) {
                Card(
                    modifier = Modifier.padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                    border = if(maritalStatus == it) BorderStroke(2.dp, tintColor) else null,
                    onClick = { maritalStatus = it }
                ) {
                    Text(text = it.title, modifier = Modifier.padding(15.dp))
                }
            }
        }

        BaseOutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Номер телефона"
        )

        BaseOutlinedTextField(
            value = phoneNumberCode,
            onValueChange = { phoneNumberCode = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = "Телефонный код"
        )

        AnimatedVisibility(visible = enlistmentOfficeId != null && phoneNumber.isNotEmpty() && phoneNumberCode.toIntOrNull() != null) {
            Button(onClick = {
                scope.launch {
                    try {
                        val token = userDataStore.getAccessToken()
                        val accounting = CreateAccounting(status, maritalStatus, phoneNumber, phoneNumberCode.toInt(), enlistmentOfficeId!!)
                        val response = networkApi.userAddAccounting(userId, accounting, "Bearer $token")

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