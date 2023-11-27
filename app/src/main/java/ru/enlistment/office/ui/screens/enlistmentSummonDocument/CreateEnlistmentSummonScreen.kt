package ru.enlistment.office.ui.screens.enlistmentSummonDocument

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ru.enlistment.office.data.network.model.user.EnlistmentSummonDocumentType
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.theme.tintColor

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
        LazyRow {
            items(EnlistmentSummonDocumentType.values()) {
                Card(
                    modifier = Modifier.padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                    border = if(type == it) BorderStroke(2.dp, tintColor) else null,
                    onClick = { type = it }
                ) {
                    Text(text = it.title, modifier = Modifier.padding(15.dp))
                }
            }
        }

        Button(onClick = {
            try {
                scope.launch {
                    val token = userDataStore.getAccessToken()
                    val response = networkApi.addEnlistmentSummon(accountId, type, "Bearer $token")

                    if(response.isSuccessful)
                        navController.navigateUp()
                    else
                        Toast.makeText(context, response.errorBody()?.string().toString(), Toast.LENGTH_SHORT).show()
                }
            }catch (_: Exception) {}
        }) {
            Text(text = "Отправить")
        }
    }
}