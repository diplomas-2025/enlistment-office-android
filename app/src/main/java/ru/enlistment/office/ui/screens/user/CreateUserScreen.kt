package ru.enlistment.office.ui.screens.user

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.networkApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkApi = remember { networkApi }
    val userDataStore = remember { UserDataStore(context) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.padding(5.dp),
            shape = AbsoluteRoundedCornerShape(15.dp),
            label = { Text(text = "Почта") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.padding(5.dp),
            shape = AbsoluteRoundedCornerShape(15.dp),
            label = { Text(text = "Пароль") }
        )

        AnimatedVisibility(visible = email.isNotEmpty() && password.isNotEmpty()) {
            Button(onClick = {
                try {
                    scope.launch {
                        val token = userDataStore.getAccessToken()
                        val response = networkApi.addUser(email, password, "Bearer $token")

                        if(response.isSuccessful)
                            navController.navigateUp()
                        else
                            Toast.makeText(context, response.errorBody()?.string().toString(), Toast.LENGTH_SHORT).show()
                    }
                }catch (_: Exception) {}
            }) {
                Text(text = "Добавить")
            }
        }
    }
}