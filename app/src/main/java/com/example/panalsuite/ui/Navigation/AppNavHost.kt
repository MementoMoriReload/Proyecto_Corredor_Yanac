package com.example.panalsuite.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.panalsuite.model.User
import com.example.panalsuite.ui.AppBottomBar
import com.example.panalsuite.ui.BottomBarItem
import com.example.panalsuite.ui.CreateTicketScreen
import com.example.panalsuite.ui.DashboardScreen
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.ui.LoginScreen
import com.example.panalsuite.ui.ProfileScreen
import com.example.panalsuite.ui.RegisterUserScreen
import com.example.panalsuite.ui.TicketDetailScreen
import com.example.panalsuite.ui.UserManagementScreen
import com.example.panalsuite.viewmodel.AuthViewModel
import com.example.panalsuite.viewmodel.RegisterViewModel
import com.example.panalsuite.viewmodel.TicketViewModel

private const val ACTION_LOGOUT = "action_logout"

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel = viewModel(),
    ticketViewModel: TicketViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val user by authViewModel.currentUser.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val startDestination = remember {
        if (authViewModel.currentUser.value != null) Routes.TICKET_LIST else Routes.LOGIN
    }

    val logoutAndGoToLogin: () -> Unit = {
        authViewModel.logout()
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val toDashboard: () -> Unit = {
        navController.navigate(Routes.DASHBOARD) {
            popUpTo(route = Routes.TICKET_LIST)
            launchSingleTop = true
        }
    }

    val toRegister: () -> Unit = {
        navController.navigate(Routes.REGISTER) {
            launchSingleTop = true
        }
    }

    val toManagement: () -> Unit = {
        navController.navigate(Routes.MANAGEMENT) {
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != Routes.LOGIN) {
                AppBottomBar(
                    items = menuItemsFor(user),
                    currentRoute = currentRoute,
                    onItemClick = { (route) ->
                        if (route == ACTION_LOGOUT) {
                            logoutAndGoToLogin()
                        } else {
                            navController.navigate(route) {
                                popUpTo(route = Routes.TICKET_LIST)
                                launchSingleTop = true
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300),
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300),
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300),
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300),
                ) + fadeOut(animationSpec = tween(300))
            },
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(authViewModel = authViewModel) {
                    navController.navigate(Routes.TICKET_LIST) {
                        popUpTo(route = Routes.LOGIN) { inclusive = true }
                    }
                }
            }

            composable(Routes.REGISTER) {
                val registerViewModel: RegisterViewModel = viewModel()

                user?.let { currentUser ->
                    RegisterUserScreen(
                        user = currentUser,
                        authViewModel = authViewModel,
                        registerViewModel = registerViewModel,
                        onRegisterSuccess = {
                            navController.navigate(Routes.TICKET_LIST) {
                                popUpTo(route = Routes.TICKET_LIST) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Routes.TICKET_LIST) {
                user?.let { currentUser ->
                    HomeScreen(
                        user = currentUser,
                        ticketViewModel = ticketViewModel,
                        onTicketClick = { id ->
                            navController.navigate(Routes.ticketDetail(id))
                        },
                        onDashboardClick = toDashboard,
                        onManagementClick = toManagement,
                        onRegisterClick = toRegister,
                        onProfileClick = {
                            navController.navigate(Routes.PROFILE) {
                                launchSingleTop = true
                            }
                        },
                        onLogoutClick = logoutAndGoToLogin
                    )
                }
            }

            composable(Routes.PROFILE) {
                user?.let { currentUser ->
                    ProfileScreen(
                        user = currentUser,
                        onBackClick = { navController.popBackStack() },
                        onLogoutClick = logoutAndGoToLogin
                    )
                }
            }

            composable(
                route = Routes.TICKET_DETAIL,
                arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
            ) { entry ->
                val ticketId = entry.arguments?.getInt("ticketId") ?: 0
                val tickets by ticketViewModel.tickets.collectAsState()
                val currentTicket = tickets.find { it.id == ticketId }

                user?.let { currentUser ->
                    TicketDetailScreen(
                        ticket = currentTicket,
                        user = currentUser,
                        ticketViewModel = ticketViewModel,
                        onBackClick = { navController.popBackStack() },
                        onDashboardClick = toDashboard,
                        onManagementClick = toManagement,
                        onLogoutClick = logoutAndGoToLogin
                    )
                }
            }

            composable(Routes.CREATE_TICKET) {
                user?.let { currentUser ->
                    CreateTicketScreen(
                        user = currentUser,
                        ticketViewModel = ticketViewModel,
                        onBackClick = { navController.popBackStack() }
                    ) {
                        navController.navigate(Routes.TICKET_LIST) {
                            popUpTo(route = Routes.TICKET_LIST) { inclusive = true }
                        }
                    }
                }
            }

            composable(Routes.DASHBOARD) {
                user?.let { currentUser ->
                    DashboardScreen(
                        user = currentUser,
                        ticketViewModel = ticketViewModel,
                        onBackClick = { navController.popBackStack() },
                        onProfileClick = {
                            navController.navigate(Routes.PROFILE) {
                                launchSingleTop = true
                            }
                        },
                        onLogoutClick = logoutAndGoToLogin
                    )
                }
            }

            composable(Routes.MANAGEMENT) {
                val usersList by authViewModel.users.collectAsState()

                user?.let { currentUser ->
                    UserManagementScreen(
                        user = currentUser,
                        userList = usersList,
                        onBackClick = { navController.popBackStack() },
                        onRoleChange = { targetUser, newRole ->
                            authViewModel.updateUserRole(targetUser.id, newRole)
                        }
                    )
                }
            }
        }
    }
}

private fun menuItemsFor(user: User?): List<BottomBarItem> {
    if (user == null) {
        return emptyList()
    }
    return buildList {
        add(
            BottomBarItem(
                route = Routes.TICKET_LIST,
                label = "Bandeja",
                selectedRoutes = listOf(Routes.TICKET_LIST, Routes.TICKET_DETAIL),
            )
        )
        if (user.role.canCreateTicket) {
            add(BottomBarItem(Routes.CREATE_TICKET, "Nuevo"))
        }
        if (user.role.canSeeDashboard) {
            add(BottomBarItem(Routes.DASHBOARD, "Resumen"))
        }
        add(BottomBarItem(ACTION_LOGOUT, "Salir"))
    }
}