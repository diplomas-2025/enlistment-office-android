package ru.enlistment.office.ui.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.enlistment.office.common.setClipboard
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.data.network.model.user.User
import ru.enlistment.office.data.network.networkApi
import ru.enlistment.office.ui.view.dialog.EditItemDialog

private enum class TabState(val title: String) {
    BASE_INFO("Данные"),
    WORD("Работа"),
    COLLEGE("Учёба"),
    PASSPORT("Паспорт")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    navController: NavController,
    userId: Int?
) {
    val context = LocalContext.current
    val networkApi = remember { networkApi }
    val userDataStore = remember { UserDataStore(context) }
    var user by remember { mutableStateOf<User?>(null) }
    val isAdmin by remember { mutableStateOf(userDataStore.getIsAdmin()) }
    var currentTabStateIndex by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = Unit, block = {
        try {
            val token = userDataStore.getAccessToken()

            val response = if (userId != null)
                networkApi.getUserById(userId, "Bearer $token")
            else
                networkApi.getUserInfo("Bearer $token")

            response.body()?.let { user = it }
        } catch (_: Exception) {
        }
    })

    Scaffold(
        floatingActionButton = {
            if (user?.accounting != null) {
                Button(
                    onClick = { navController.navigate("add_enlistment_summon/${user!!.id}") },
                    modifier = Modifier.padding(horizontal = 3.dp)
                ) {
                    Text("Отправить повестку")
                }
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                user?.let {

                    TopAppBar(
                        title = {
                            Text(
                                text = it.email,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        actions = {
                            if (userId == null) {
                                IconButton(onClick = {
                                    userDataStore.clear()
                                    navController.navigate("auth") {
                                        popUpTo("auth") {
                                            inclusive = true
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Выход",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                TabRow(
                    selectedTabIndex = currentTabStateIndex, // Index of the selected tab
                    containerColor = MaterialTheme.colorScheme.primary, // Background color for the TabRow
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    TabState.values().forEach {
                        Tab(
                            selected = currentTabStateIndex == it.ordinal,
                            onClick = {
                                currentTabStateIndex = it.ordinal // Update the selected tab index
                            },
                            text = { Text(it.title) } // Display the tab text
                        )
                    }
                }

                if (currentTabStateIndex == 0) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp)
                            .fillMaxWidth()
                    ) {
                        // Show action button to add/edit work info, only if the user is an admin and has accounting data
                        if (isAdmin && user != null) {
                            Spacer(Modifier.height(3.dp))
                            Button(
                                onClick = {
                                    if (user!!.accounting != null) {
                                        navController.navigate("add_accounting/${user!!.id}?update=true")
                                    } else {
                                        navController.navigate("add_accounting/${user!!.id}?update=false")
                                    }
                                },
                                modifier = Modifier
                                    .padding(start = 16.dp) // Spacing between text and button
                                    .height(36.dp) // Adjusted height for compactness
                                    .widthIn(min = 100.dp), // Slightly smaller width
                                shape = MaterialTheme.shapes.medium, // Rounded corners
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // Primary color for button
                                    contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                                )
                            ) {
                                Text(
                                    text = if (user!!.accounting != null) "Редактировать" else "Добавить",
                                    style = MaterialTheme.typography.bodyMedium, // Smaller text style
                                    modifier = Modifier
                                        .fillMaxHeight() // Ensures text takes up the full height
                                        .padding(horizontal = 12.dp), // Horizontal padding
                                    textAlign = TextAlign.Center // Ensures text is centered
                                )
                            }
                        }
                    }

                    user?.accounting?.let {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                RowContentText(
                                    leftText = "Статус",
                                    rightText = it.status.title
                                )

                                RowContentText(
                                    leftText = "Семейное положение",
                                    rightText = it.maritalStatus.title
                                )

                                RowContentText(
                                    leftText = "Номер телефона",
                                    rightText = it.phoneNumber
                                )

                                RowContentText(
                                    leftText = "Телефонный код",
                                    rightText = it.phoneNumberCode.toString()
                                )

                                RowContentText(
                                    leftText = "Военкомат",
                                    rightText = "г. ${it.enlistmentOffice.city} ул. ${it.enlistmentOffice.street}, ${it.enlistmentOffice.houseNumber}"
                                )
                            }
                        }
                    }

                    if (user?.accounting?.enlistmentSummonDocuments != null && user?.accounting?.enlistmentSummonDocuments!!.isNotEmpty()) {

                        Text(
                            text = "Повестки",
                            fontWeight = FontWeight.W900,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(top = 15.dp, bottom = 5.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        LazyRow {
                            items(user?.accounting!!.enlistmentSummonDocuments) {
                                Card(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .widthIn(max = 250.dp),
                                    shape = AbsoluteRoundedCornerShape(15.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        RowContentText(
                                            leftText = "Тип",
                                            rightText = it.type.title
                                        )

                                        RowContentText(
                                            leftText = "Дата",
                                            rightText = it.sendDate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (currentTabStateIndex == 1) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp)
                            .fillMaxWidth()
                    ) {
                        // Show action button to add/edit work info, only if the user is an admin and has accounting data
                        if (isAdmin && user?.accounting != null) {
                            Spacer(Modifier.height(3.dp))
                            Button(
                                onClick = {
                                    if (user?.accounting?.work != null) {
                                        navController.navigate("add_work/${user?.id}?update=true")
                                    } else {
                                        navController.navigate("add_work/${user?.id}?update=false")
                                    }
                                },
                                modifier = Modifier
                                    .padding(start = 16.dp) // Spacing between text and button
                                    .height(36.dp) // Adjusted height for compactness
                                    .widthIn(min = 100.dp), // Slightly smaller width
                                shape = MaterialTheme.shapes.medium, // Rounded corners
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // Primary color for button
                                    contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                                )
                            ) {
                                Text(
                                    text = if (user?.accounting?.work != null) "Редактировать" else "Добавить",
                                    style = MaterialTheme.typography.bodyMedium, // Smaller text style
                                    modifier = Modifier
                                        .fillMaxHeight() // Ensures text takes up the full height
                                        .padding(horizontal = 12.dp), // Horizontal padding
                                    textAlign = TextAlign.Center // Ensures text is centered
                                )
                            }
                        }
                    }

                    user?.accounting?.work?.let {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                RowContentText(
                                    leftText = "Организация",
                                    rightText = it.organization
                                )

                                RowContentText(
                                    leftText = "Должность",
                                    rightText = it.position
                                )

                                RowContentText(
                                    leftText = "Отсрочка",
                                    rightText = if (it.postponement) "Да" else "Нет"
                                )
                            }
                        }
                    }
                }

                if (currentTabStateIndex == 2) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp)
                            .fillMaxWidth()
                    ) {
                        // Show action button to add/edit work info, only if the user is an admin and has accounting data
                        if (isAdmin && user?.accounting != null) {
                            Spacer(Modifier.height(3.dp))
                            Button(
                                onClick = {
                                    if (user?.accounting?.study != null)
                                        navController.navigate("add_study/${user?.id}?update=true")
                                    else
                                        navController.navigate("add_study/${user?.id}?update=false")
                                },
                                modifier = Modifier
                                    .padding(start = 16.dp) // Spacing between text and button
                                    .height(36.dp) // Adjusted height for compactness
                                    .widthIn(min = 100.dp), // Slightly smaller width
                                shape = MaterialTheme.shapes.medium, // Rounded corners
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // Primary color for button
                                    contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                                )
                            ) {
                                Text(
                                    text = if (user?.accounting?.study != null) "Редактировать" else "Добавить",
                                    style = MaterialTheme.typography.bodyMedium, // Smaller text style
                                    modifier = Modifier
                                        .fillMaxHeight() // Ensures text takes up the full height
                                        .padding(horizontal = 12.dp), // Horizontal padding
                                    textAlign = TextAlign.Center // Ensures text is centered
                                )
                            }
                        }
                    }

                    user?.accounting?.study?.let {

                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                RowContentText(
                                    leftText = "Название",
                                    rightText = it.name
                                )

                                RowContentText(
                                    leftText = "Курс",
                                    rightText = it.currentCourse.toString()
                                )

                                RowContentText(
                                    leftText = "Начала обучение",
                                    rightText = it.startDate
                                )

                                RowContentText(
                                    leftText = "Конец обучение",
                                    rightText = it.endDate
                                )

                                RowContentText(
                                    leftText = "Отсрочка",
                                    rightText = if (it.postponement) "Да" else "Нет"
                                )
                            }
                        }
                    }
                }

                if (currentTabStateIndex == 3) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp)
                            .fillMaxWidth()
                    ) {
                        // Check if the user has accounting info and display the title

                        // Show action button to add/edit work info, only if the user is an admin and has accounting data
                        if (isAdmin && user?.accounting != null) {
                            Spacer(Modifier.height(3.dp))
                            Button(
                                onClick = {
                                    if (user?.accounting?.passport != null)
                                        navController.navigate("add_passport/${user?.id}?update=true")
                                    else
                                        navController.navigate("add_passport/${user?.id}?update=false")
                                },
                                modifier = Modifier
                                    .padding(start = 16.dp) // Spacing between text and button
                                    .height(36.dp) // Adjusted height for compactness
                                    .widthIn(min = 100.dp), // Slightly smaller width
                                shape = MaterialTheme.shapes.medium, // Rounded corners
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // Primary color for button
                                    contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                                )
                            ) {
                                Text(
                                    text = if (user?.accounting?.passport != null) "Редактировать" else "Добавить",
                                    style = MaterialTheme.typography.bodyMedium, // Smaller text style
                                    modifier = Modifier
                                        .fillMaxHeight() // Ensures text takes up the full height
                                        .padding(horizontal = 12.dp), // Horizontal padding
                                    textAlign = TextAlign.Center // Ensures text is centered
                                )
                            }
                        }
                    }

                    user?.accounting?.passport?.let {

                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                RowContentText(
                                    leftText = "Выдан",
                                    rightText = it.issued
                                )

                                RowContentText(
                                    leftText = "Дата выдачи",
                                    rightText = it.dateIssue
                                )

                                RowContentText(
                                    leftText = "Серия",
                                    rightText = it.series
                                )

                                RowContentText(
                                    leftText = "Номер",
                                    rightText = it.number
                                )

                                RowContentText(
                                    leftText = "Имя",
                                    rightText = it.first_name
                                )

                                RowContentText(
                                    leftText = "Фамилия",
                                    rightText = it.last_name
                                )

                                RowContentText(
                                    leftText = "Отчество",
                                    rightText = it.middle_name
                                )

                                RowContentText(
                                    leftText = "День рождения",
                                    rightText = it.birthday
                                )

                                RowContentText(
                                    leftText = "Адресс",
                                    rightText = "г. ${it.addressCity} ул. ${it.addressStreet}, ${it.addressHouseNumber} кв. ${it.addressApartmentNumber}"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun RowContentText(
    leftText: String,
    rightText: String,
    updatable: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onRightTextChange: (String) -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var isShowEditItemDialog by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        editValue = rightText
    }

    if (isShowEditItemDialog) {
        EditItemDialog(
            value = editValue,
            onValueChange = { editValue = it },
            keyboardOptions = keyboardOptions,
            onDismissRequest = { isShowEditItemDialog = false },
            onSaveClick = { onRightTextChange(editValue) }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                if (onClick != null) onClick() else setClipboard(context, rightText)
                if (updatable) isShowEditItemDialog = true
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leftText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = rightText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
            textAlign = TextAlign.End
        )
    }

    Divider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    )
}