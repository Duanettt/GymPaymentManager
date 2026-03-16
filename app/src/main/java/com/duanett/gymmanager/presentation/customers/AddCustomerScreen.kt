package com.duanett.gymmanager.presentation.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duanett.gymmanager.domain.model.MembershipType
import com.duanett.gymmanager.presentation.customers.AddCustomerContract.Effect
import com.duanett.gymmanager.presentation.customers.AddCustomerContract.Intent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AddCustomerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack    -> onNavigateBack()
                is Effect.ShowSnackbar    -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Customer",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(Intent.CancelClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // imePadding() shifts the bottom buttons up when the soft keyboard appears.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Scrollable form — grows to fill available space
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NameField(
                    value = state.name,
                    error = state.nameError,
                    onValueChange = { viewModel.handleIntent(Intent.NameChanged(it)) },
                    onFocusLost = { viewModel.handleIntent(Intent.NameFocusLost) }
                )

                PhoneField(
                    value = state.phone,
                    error = state.phoneError,
                    onValueChange = { viewModel.handleIntent(Intent.PhoneChanged(it)) },
                    onFocusLost = { viewModel.handleIntent(Intent.PhoneFocusLost) }
                )

                MembershipTypeDropdown(
                    selected = state.membershipType,
                    onSelected = { viewModel.handleIntent(Intent.MembershipTypeSelected(it)) }
                )

                StatusDropdown(
                    isActive = state.isActive,
                    onSelected = { viewModel.handleIntent(Intent.ActiveStatusChanged(it)) }
                )

                // Extra breathing room so the last field clears the divider
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalDivider()

            ActionButtons(
                isSaving = state.isSaving,
                saveEnabled = state.isFormValid && !state.isSaving,
                onCancel = { viewModel.handleIntent(Intent.CancelClicked) },
                onSave = { viewModel.handleIntent(Intent.SaveClicked) }
            )
        }
    }
}

// ─── Form fields ──────────────────────────────────────────────────────────────

@Composable
private fun NameField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (!it.isFocused) onFocusLost() },
        label = { Text("Name *") },
        placeholder = { Text("e.g. Jane Smith") },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.clearFocus() /* moves to next field via Tab order */ }
        )
    )
}

@Composable
private fun PhoneField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (!it.isFocused) onFocusLost() },
        label = { Text("Phone *") },
        placeholder = { Text("(555) 123-4567") },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    )
}

// ─── Dropdowns ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MembershipTypeDropdown(
    selected: MembershipType,
    onSelected: (MembershipType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.displayName(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Type *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MembershipType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName()) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropdown(
    isActive: Boolean,
    onSelected: (Boolean) -> Unit
) {
    val options = listOf(true to "Active", false to "Inactive")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = if (isActive) "Active" else "Inactive",
            onValueChange = {},
            readOnly = true,
            label = { Text("Status") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// ─── Action buttons ───────────────────────────────────────────────────────────

@Composable
private fun ActionButtons(
    isSaving: Boolean,
    saveEnabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            enabled = !isSaving
        ) {
            Text("Cancel")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = saveEnabled
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save")
            }
        }
    }
}
