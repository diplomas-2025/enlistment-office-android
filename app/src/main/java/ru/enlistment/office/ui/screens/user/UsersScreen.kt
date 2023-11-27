package ru.enlistment.office.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.UserShort
import ru.enlistment.office.data.network.networkApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val networkApi = remember { networkApi }
    val userDataStore = remember { UserDataStore(context) }
    var users by remember { mutableStateOf(emptyList<UserShort>()) }
    val isAdmin by remember { mutableStateOf(userDataStore.getIsAdmin()) }

    LaunchedEffect(key1 = Unit, block = {
        val token = userDataStore.getAccessToken()
        val response = networkApi.getUserAll("Bearer $token")
        response.body()?.let { users = it }
    })

    LazyColumn {
        item {
            if(isAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { navController.navigate("create_user") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        items(users) {user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = AbsoluteRoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                onClick = { navController.navigate("user?userId=${user.id}") }
            ) {
                Text(
                    text = if(user.firstName != null && user.lastName != null)
                        "${user.lastName} ${user.firstName} ${user.middleName}"
                    else
                        user.email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}