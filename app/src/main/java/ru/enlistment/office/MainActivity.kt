package ru.enlistment.office

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.enlistment.office.data.database.UserDataStore
import ru.enlistment.office.ui.screens.auth.AuthScreen
import ru.enlistment.office.ui.screens.enlistmentSummonDocument.CreateEnlistmentSummonScreen
import ru.enlistment.office.ui.screens.user.CreateAccountingScreen
import ru.enlistment.office.ui.screens.user.CreatePassportScreen
import ru.enlistment.office.ui.screens.user.CreateStudyScreen
import ru.enlistment.office.ui.screens.user.CreateUserScreen
import ru.enlistment.office.ui.screens.user.CreateWorkScreen
import ru.enlistment.office.ui.screens.user.UserInfoScreen
import ru.enlistment.office.ui.screens.user.UsersScreen
import ru.enlistment.office.ui.theme.EnlistmentOfficeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val userDataStore = remember { UserDataStore(this) }

            EnlistmentOfficeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if(userDataStore.getAccessToken() == null)
                            "auth"
                        else
                            "user",
                        builder = {
                            composable("auth") { AuthScreen(navController) }
                            composable("user?userId={userId}", arguments = listOf(
                                navArgument("userId") {
                                    type = NavType.StringType
                                    nullable = true
                                }
                            )) {
                                UserInfoScreen(
                                    navController = navController,
                                    userId = try {
                                        it.arguments?.getString("userId")?.toIntOrNull()
                                    }catch (e: Exception) { null }
                                )
                            }
                            composable("users") { UsersScreen(navController = navController) }
                            composable("create_user") { CreateUserScreen(navController) }
                            composable(
                                route = "add_enlistment_summon/{id}",
                                arguments = listOf(
                                    navArgument("id") {
                                        type = NavType.IntType
                                    }
                                )
                            ){
                                CreateEnlistmentSummonScreen(
                                    navController = navController,
                                    accountId = it.arguments!!.getInt("id")
                                )
                            }
                            composable("add_accounting/{userId}?update={update}", arguments = listOf(
                                navArgument("update") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                },
                                navArgument("userId") {
                                    type = NavType.IntType
                                    nullable = false
                                }
                            )) {
                                CreateAccountingScreen(
                                    navController = navController,
                                    update = it.arguments!!.getBoolean("update"),
                                    userId = it.arguments!!.getInt("userId")
                                )
                            }
                            composable("add_work/{accountId}?update={update}", arguments = listOf(
                                navArgument("update") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                },
                                navArgument("accountId") {
                                    type = NavType.IntType
                                    nullable = false
                                }
                            )) {
                                CreateWorkScreen(
                                    navController = navController,
                                    update = it.arguments!!.getBoolean("update"),
                                    accountId = it.arguments!!.getInt("accountId")
                                )
                            }
                            composable("add_study/{accountId}?update={update}", arguments = listOf(
                                navArgument("update") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                },
                                navArgument("accountId") {
                                    type = NavType.IntType
                                    nullable = false
                                }
                            )) {
                                CreateStudyScreen(
                                    navController = navController,
                                    update = it.arguments!!.getBoolean("update"),
                                    accountId = it.arguments!!.getInt("accountId")
                                )
                            }
                            composable("add_passport/{accountId}?update={update}", arguments = listOf(
                                navArgument("update") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                },
                                navArgument("accountId") {
                                    type = NavType.IntType
                                    nullable = false
                                }
                            )) {
                                CreatePassportScreen(
                                    navController = navController,
                                    update = it.arguments!!.getBoolean("update"),
                                    accountId = it.arguments!!.getInt("accountId")
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}