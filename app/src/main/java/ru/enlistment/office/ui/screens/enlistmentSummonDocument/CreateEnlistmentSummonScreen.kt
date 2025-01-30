package ru.enlistment.office.ui.screens.enlistmentSummonDocument

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.EnlistmentSummonDocumentType
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.theme.tintColor
import ru.enlistment.office.ui.view.BaseLottieAnimation
import ru.enlistment.office.ui.view.LottieAnimationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEnlistmentSummonScreen(
    navController: NavController,
    accountId: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDataStore = remember { UserDataStore(context) }
    val networkApi = remember { networkApi }
    var type by remember { mutableStateOf(EnlistmentSummonDocumentType.ARMY) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Карточка с выбором типа повестки
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

                Text(text = "Выберите тип повестки", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow {
                    items(EnlistmentSummonDocumentType.values()) { item ->
                        Card(
                            modifier = Modifier.padding(5.dp),
                            shape = RoundedCornerShape(15.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                            border = if (type == item) BorderStroke(2.dp, tintColor) else null,
                            onClick = { type = item }
                        ) {
                            Text(text = item.title, modifier = Modifier.padding(15.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка отправки
                Button(onClick = {
                    try {
                        scope.launch {
                            val token = userDataStore.getAccessToken()
                            val response = networkApi.addEnlistmentSummon(accountId, type, "Bearer $token")

                            if (response.isSuccessful)
                                navController.navigateUp()
                            else
                                Toast.makeText(context, response.errorBody()?.string().toString(), Toast.LENGTH_SHORT).show()
                        }
                    } catch (_: Exception) {}
                }) {
                    Text(text = "Отправить")
                }
            }
        }
    }
}