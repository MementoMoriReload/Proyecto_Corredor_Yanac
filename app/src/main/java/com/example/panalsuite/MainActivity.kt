package com.example.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.panalsuite.ui.navigation.AppNavHost
import com.example.panalsuite.ui.theme.PanalSuiteTheme


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
                AppNavHost()

            }
        }
    }
}