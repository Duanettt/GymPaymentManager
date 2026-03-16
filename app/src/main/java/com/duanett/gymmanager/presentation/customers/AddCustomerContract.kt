package com.duanett.gymmanager.presentation.customers

import com.duanett.gymmanager.domain.model.MembershipType

object AddCustomerContract {

    data class State(
        val name: String = "",
        val phone: String = "",
        val membershipType: MembershipType = MembershipType.MONTHLY,
        val isActive: Boolean = true,
        /** True once the field has been focused then left — gates error display. */
        val nameTouched: Boolean = false,
        val phoneTouched: Boolean = false,
        val isSaving: Boolean = false
    ) {
        val nameError: String?
            get() = if (nameTouched && name.isBlank()) "Name is required" else null

        val phoneError: String?
            get() = if (phoneTouched && phone.isBlank()) "Phone is required" else null

        /** Save button is only enabled when both required fields are filled. */
        val isFormValid: Boolean
            get() = name.isNotBlank() && phone.isNotBlank()
    }

    sealed class Intent {
        data class NameChanged(val value: String) : Intent()
        /** Sent when the name field loses focus — triggers inline error display. */
        data object NameFocusLost : Intent()

        data class PhoneChanged(val value: String) : Intent()
        /** Sent when the phone field loses focus — triggers inline error display. */
        data object PhoneFocusLost : Intent()

        data class MembershipTypeSelected(val type: MembershipType) : Intent()
        data class ActiveStatusChanged(val isActive: Boolean) : Intent()

        data object SaveClicked : Intent()
        data object CancelClicked : Intent()
    }

    sealed class Effect {
        data object NavigateBack : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }
}
