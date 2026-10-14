package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.panalsuite.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authviewModel: AuthViewModel,
    onLoginSuccess: () -> Unit

    ){
        val state by authviewModel.loginState.collectAsState()

        LoginScreenContent(
            email = state.email,
            password = state.password,
            error = state.error,
            onEmailChange = authviewModel::onEmailChange,
            onPasswordChange = authviewModel::onPasswordChange,
            onLoginClick = {
                if (authviewModel.login()) onLoginSuccess()
            }
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PanelSuite") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16365D),
                    titleContentColor = Color.White
                )
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo") },
                singleLine = true,
                isError = error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = error != null,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Ocultar" else "Ver")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }
        }
    }
}

@Composable
private fun DemoUser(
    roleLabel: String,
    email: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    TextButton(
        onClick = {
            onEmailChange(email)
            onPasswordChange("1234")
        }
    ) {
        Text("$roleLabel · $email")
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenContentPreview() {
    LoginScreenContent(
        email = "john@panalbit.test",
        password = "1234",
        error = null,
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenContentErrorPreview() {
    LoginScreenContent(
        email = "john@panalbit.test",
        password = "mal",
        error = "Correo o contraseña incorrectos",
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}