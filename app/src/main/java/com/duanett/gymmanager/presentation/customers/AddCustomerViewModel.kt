package com.duanett.gymmanager.presentation.customers

import androidx.lifecycle.viewModelScope
import com.duanett.gymmanager.data.repository.CustomerRepository
import com.duanett.gymmanager.domain.model.Customer
import com.duanett.gymmanager.presentation.customers.AddCustomerContract.Effect
import com.duanett.gymmanager.presentation.customers.AddCustomerContract.Intent
import com.duanett.gymmanager.presentation.customers.AddCustomerContract.State
import com.duanett.gymmanager.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
) : MviViewModel<State, Intent, Effect>(State()) {

    override fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.NameChanged            -> setState { copy(name = intent.value) }
            is Intent.NameFocusLost          -> setState { copy(nameTouched = true) }
            is Intent.PhoneChanged           -> setState { copy(phone = formatPhone(intent.value)) }
            is Intent.PhoneFocusLost         -> setState { copy(phoneTouched = true) }
            is Intent.MembershipTypeSelected -> setState { copy(membershipType = intent.type) }
            is Intent.ActiveStatusChanged    -> setState { copy(isActive = intent.isActive) }
            is Intent.SaveClicked            -> onSave()
            is Intent.CancelClicked          -> sendEffect(Effect.NavigateBack)
        }
    }

    private fun onSave() {
        // Touch all fields before checking validity so errors appear if Save is tapped early.
        setState { copy(nameTouched = true, phoneTouched = true) }
        if (!state.value.isFormValid) return

        viewModelScope.launch {
            setState { copy(isSaving = true) }
            val s = state.value
            runCatching {
                repository.saveCustomer(
                    Customer(
                        name = s.name.trim(),
                        phone = s.phone.trim(),
                        email = "",
                        membershipType = s.membershipType,
                        isActive = s.isActive
                    )
                )
            }.onSuccess {
                // Navigate back — CustomerListScreen's Flow will pick up the new row.
                sendEffect(Effect.NavigateBack)
            }.onFailure { e ->
                setState { copy(isSaving = false) }
                sendEffect(Effect.ShowSnackbar("Failed to save: ${e.message}"))
            }
        }
    }

    /**
     * Formats raw digit input as (XXX) XXX-XXXX while the user types.
     * Strips all non-digits and caps at 10 digits, so the transformation is
     * idempotent — running it again on already-formatted text is safe.
     */
    private fun formatPhone(input: String): String {
        val digits = input.filter { it.isDigit() }.take(10)
        return buildString {
            if (digits.isEmpty()) return@buildString
            append("(")
            append(digits.take(3))
            if (digits.length > 3) {
                append(") ")
                append(digits.drop(3).take(3))
            }
            if (digits.length > 6) {
                append("-")
                append(digits.drop(6))
            }
        }
    }
}
