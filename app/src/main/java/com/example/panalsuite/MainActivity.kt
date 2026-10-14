package com.example.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.repository.TicketRepository
import com.example.panalsuite.ui.theme.PanalSuiteTheme
import com.example.panalsuite.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //onCreate = Pantalla se crea
        //onStart onResume == la pantalla se vuekve vusuvke y toma el foco
        //onPause onStop == el usuario cambio de app o bloqueo el telefono
        //onDestroy == la pantalla se cierra o el sistema lo mata

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanalSuiteTheme {
                HomeScreen(
                    user = User("u1", "Victor Castro", "victor@panal.cl",null, UserRole.REQUESTER),
                    ticketViewModel = TicketViewModel(),
                    onTicketClick = {ticketId -> },
                    onCreateTicket = {},
                    onOpenDashboard = {},
                    onLogout ={}

                )

                }
            }
        }
    }