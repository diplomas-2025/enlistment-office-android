package ru.enlistment.office.ui.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import ru.enlistment.office.ui.theme.tintColor
import ru.enlistment.office.ui.view.dialog.EditItemDialog

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

    LaunchedEffect(key1 = Unit, block = {
        try {
            val token = userDataStore.getAccessToken()

            val response = if(userId != null)
                networkApi.getUserById(userId, "Bearer $token")
            else
                networkApi.getUserInfo("Bearer $token")

            response.body()?.let { user = it }
        }catch (_: Exception){}
    })

    LazyColumn {
        item {
            user?.let {

                TopAppBar(
                    title = { Text(text = "Идентификатор #${it.id}") },
                    actions = {
                        if(isAdmin) {
                            IconButton(onClick = { navController.navigate("users") }) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = null
                                )
                            }

                            IconButton(
                                onClick = { navController.navigate("add_accounting/${user!!.id}?update=true") },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }

                            user?.accounting?.let {
                                IconButton(onClick = { navController.navigate("add_enlistment_summon/${user!!.id}") }) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                )

                Divider(color = tintColor)

                RowContentText(
                    leftText = "Почта",
                    rightText = it.email
                )
            }

            user?.accounting?.let {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if(isAdmin && user != null && user!!.accounting == null ) {
                    Button(onClick = { navController.navigate("add_accounting/${user!!.id}?update=false") }) {
                        Text(text = "Добавить инфориацию")
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Работа",
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                if(isAdmin) {
                    IconButton(onClick = {
                        if(user?.accounting?.work != null)
                            navController.navigate("add_work/${user?.id}?update=true")
                        else
                            navController.navigate("add_work/${user?.id}?update=false")
                    }) {
                        Icon(
                            imageVector = if(user?.accounting?.work != null)
                                Icons.Default.Edit
                            else
                                Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }

            user?.accounting?.work?.let {

                Divider(color = tintColor)

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
                    rightText = if(it.postponement) "Да" else "Нет"
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Учебное заведение",
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                if(isAdmin) {
                    IconButton(onClick = {
                        if(user?.accounting?.study != null)
                            navController.navigate("add_study/${user?.id}?update=true")
                        else
                            navController.navigate("add_study/${user?.id}?update=false")
                    }) {
                        Icon(
                            imageVector = if(user?.accounting?.study != null)
                                Icons.Default.Edit
                            else
                                Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }

            user?.accounting?.study?.let {

                Divider(color = tintColor)

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
                    rightText = if(it.postponement) "Да" else "Нет"
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Паспорт",
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                if(isAdmin) {
                    IconButton(onClick = {
                        if(user?.accounting?.passport != null)
                            navController.navigate("add_passport/${user?.id}?update=true")
                        else
                            navController.navigate("add_passport/${user?.id}?update=false")
                    }) {
                        Icon(
                            imageVector = if(user?.accounting?.passport != null)
                                Icons.Default.Edit
                            else
                                Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }

            user?.accounting?.passport?.let {

                Divider(color = tintColor)

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

            if(user?.accounting?.enlistmentSummonDocuments != null && user?.accounting?.enlistmentSummonDocuments!!.isNotEmpty()) {

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

                Spacer(modifier = Modifier.height(30.dp))
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

    LaunchedEffect(key1 = Unit, block = {
        editValue = rightText
    })

    if(isShowEditItemDialog) {
        EditItemDialog(
            value = editValue,
            onValueChange = { editValue = it},
            keyboardOptions = keyboardOptions,
            onDismissRequest = {
                isShowEditItemDialog = false
            },
            onSaveClick = {
                onRightTextChange(editValue)
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clickable {
                if (onClick != null) onClick() else setClipboard(context, rightText)
                if (updatable) isShowEditItemDialog = true
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leftText,
            modifier = Modifier.padding(5.dp)
        )

        Text(
            text = rightText,
            modifier = Modifier.padding(5.dp),
            textAlign = TextAlign.End
        )
    }

    Divider(color = tintColor)
}