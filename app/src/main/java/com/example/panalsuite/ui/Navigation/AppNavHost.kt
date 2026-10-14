package com.example.panalsuite.ui.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.panalsuite.model.User
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.ui.LoginScreen
import com.example.panalsuite.ui.AppBottomBar
import com.example.panalsuite.ui.BottomBarItem
import com.example.panalsuite.ui.TicketDetailScreen
import com.example.panalsuite.viewmodel.AuthViewModel
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

    Scaffold(
        bottomBar = {
            AppBottomBar(
                items = menuItemsFor(user),
                currentRoute = currentRoute,
                onItemClick = { item ->
                    if (item.route == ACTION_LOGOUT) {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true } // vacía todo el historial
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(Routes.TICKET_LIST)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    authviewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Routes.TICKET_LIST) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.TICKET_LIST) {
                user?.let { currentUser ->
                    HomeScreen(
                        user = currentUser,
                        ticketViewModel = ticketViewModel,
                        onTicketClick = { id ->
                            navController.navigate(Routes.ticketDetail(id))
                        }
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
                        onDashboardClick = {
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.TICKET_LIST)
                                launchSingleTop = true
                            }
                        },
                        onLogoutClick = {
                            authViewModel.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Routes.CREATE_TICKET) {
                // TODO: reemplazar por CreateTicketScreen
                PlaceholderScreen(title = "Nuevo ticket")
            }

            composable(Routes.DASHBOARD) {
                // TODO: reemplazar por DashboardScreen
                PlaceholderScreen(title = "Resumen")
            }
        }
    }
}

/**
 * Qué opciones muestra el menú según la sesión y el rol.
 * Las reglas salen de UserRole, así no se duplican aquí.
 */
private fun menuItemsFor(user: User?): List<BottomBarItem> {
    if (user == null) {
        return listOf(BottomBarItem(Routes.LOGIN, "Iniciar sesión"))
    }
    return buildList {
        add(
            BottomBarItem(
                route = Routes.TICKET_LIST,
                label = "Bandeja",
                // También queda activa mientras se ve el detalle de un ticket
                selectedRoutes = listOf(Routes.TICKET_LIST, Routes.TICKET_DETAIL)
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

/** Pantalla provisoria para poder navegar mientras se construyen las reales. */
@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: (() -> Unit)? = null
) {
    Scaffold(
        topBar = { TopBar(title = title, onBackClick = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla en construcción")
        }
    }
}

@Composable
fun TopBar(title: String, onBackClick: (() -> Unit)?) {
    TODO("Not yet implemented")
}
