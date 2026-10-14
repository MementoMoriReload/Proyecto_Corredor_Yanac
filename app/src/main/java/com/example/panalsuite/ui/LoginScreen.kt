package com.example.panalsuite.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.panalsuite.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
    ){

            }
}

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = email,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = password,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    color = MaterialTheme.colorScheme.error,
                )
            }


            Button(
        onClick = {
}

}

}