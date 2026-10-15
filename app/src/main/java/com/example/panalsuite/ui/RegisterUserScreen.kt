package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.viewmodel.AuthViewModel
import com.example.panalsuite.viewmodel.RegisterUiState
import com.example.panalsuite.viewmodel.RegisterViewModel

@Composable
fun RegisterUserScreen(
    user: User,
    authViewModel: AuthViewModel,
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit = {}
) {
    val state by registerViewModel.uiState.collectAsStateWithLifecycle()

    RegisterUserScreenContent(
        state = state,
        canCreateUsers = user.role.canCreateUser,
        onNameChange = registerViewModel::onNameChange,
        onEmailChange = registerViewModel::onEmailChange,
        onPasswordChange = registerViewModel::onPasswordChange,
        onConfirmPasswordChange = registerViewModel::onConfirmPasswordChange,
        onRoleChange = registerViewModel::onRoleChange,
        onSubmit ={
            registerViewModel.submit()?.let { form ->
                val error = authViewModel.registerUser(
                    name = form.name,
                    email = form.email,
                    password = form.password,
                    role = form.role
                )
                if (error == null) {
                    registerViewModel.onSuccess(form.name)
                    onRegisterSuccess()
                } else {
                    registerViewModel.onError(error)
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserScreenContent(
    state: RegisterUiState,
    canCreateUsers: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onSubmit: () -> Unit
){
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var roleExpanded by rememberSaveable { mutableStateOf(false) }


    val nameError = if (state.showErrors) state.nameError else null
    val emailError = if (state.showErrors) state.emailError else null
    val passwordError = if (state.showErrors) state.passwordError else null
    val confirmError = if (state.showErrors) state.confirmPasswordError else null

    Scaffold(
        topBar = { AppTopBar(title = "Registrar usuario") }
    ) { padding ->
        if (!canCreateUsers) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Solo los administradores pueden registrar usuarios")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    isError = emailError != null || state.error != null,
                    supportingText = { emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it) } },
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
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    isError = confirmError != null,
                    supportingText = { confirmError?.let { Text(it) } },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.role.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol de usuario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        UserRole.entries.forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption.label) },
                                onClick = {
                                    onRoleChange(roleOption)
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (state.successMessage != null) {
                    Text(
                        text = state.successMessage,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Registrar usuario")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterUserScreenContentPreview() {
    RegisterUserScreenContent(
    state = RegisterUiState(
            name = "Victor Castro",
            email = "victor@panal.cl",
            password = "1234",
            confirmPassword = "1234",
            successMessage = "Usuario \"Ana Soto\" registrado correctamente"
        ),
        canCreateUsers = true,
        onNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRoleChange = {},
        onSubmit = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenErrorsPreview() {
    RegisterUserScreenContent(
        state = RegisterUiState(showErrors = true),
        canCreateUsers = true,
        onNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRoleChange = {},
        onSubmit = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenNoPermissionPreview() {
    RegisterUserScreenContent(
        state = RegisterUiState(),
        canCreateUsers = false,
        onNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRoleChange = {},
        onSubmit = {}
    )
}
