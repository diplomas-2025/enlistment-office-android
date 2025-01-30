package ru.enlistment.office.ui.screens.user

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.Work
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.view.BaseLottieAnimation
import ru.enlistment.office.ui.view.BaseOutlinedTextField
import ru.enlistment.office.ui.view.LottieAnimationType

@Composable
fun CreateWorkScreen(
    accountId: Int,
    update: Boolean,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDataStore = remember { UserDataStore(context) }
    val networkApi = remember { networkApi }

    var organization by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var postponement by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, block = {
        if (update) {
            try {
                val token = userDataStore.getAccessToken()
                val response = networkApi.getUserById(accountId, "Bearer $token")

                response.body()?.let {
                    it.accounting?.work?.let {
                        organization = it.organization
                        position = it.position
                        postponement = it.postponement
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
            // Card for Work Form
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
                        text = "Работа",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Organization Input
                    BaseOutlinedTextField(
                        value = organization,
                        onValueChange = { organization = it },
                        label = "Организация"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Position Input
                    BaseOutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = "Должность"
                    )

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
                    AnimatedVisibility(visible = organization.isNotEmpty() && position.isNotEmpty()) {
                        Button(onClick = {
                            scope.launch {
                                try {
                                    val token = userDataStore.getAccessToken()
                                    val accounting = Work(organization, position, postponement)
                                    val response = networkApi.userAddWork(
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
}