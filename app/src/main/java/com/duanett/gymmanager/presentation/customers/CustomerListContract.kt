package com.duanett.gymmanager.presentation.customers

import com.duanett.gymmanager.domain.model.Customer

/**
 * MVI contract for the Customer List screen.
 *
 * Grouping State, Intent, and Effect in one file makes it easy to
 * understand the full capabilities of a screen at a glance.
 */
object CustomerListContract {

    /**
     * Everything the UI needs to draw itself — a single immutable snapshot.
     * The Composable is a pure function of this State.
     */
    data class State(
        val customers: List<Customer> = emptyList(),
        val isLoading: Boolean = false,
        /** Non-null when something went wrong; shown as an error message. */
        val error: String? = null,
        val searchQuery: String = ""
    ) {
        /** Derived property — no need to store this separately in state. */
        val isEmpty: Boolean get() = customers.isEmpty() && !isLoading
    }

    /**
     * Every action the user can take on this screen.
     * The View sends these; the ViewModel processes them.
     * Using a sealed class ensures all cases are handled at compile time.
     */
    sealed class Intent {
        /** Trigger initial load or a manual refresh. */
        data object LoadCustomers : Intent()
        data class SearchCustomers(val query: String) : Intent()
        data class DeleteCustomer(val customer: Customer) : Intent()
        data class ToggleActiveStatus(val customer: Customer) : Intent()
        /** User tapped a customer row — navigate to their detail. */
        data class SelectCustomer(val customer: Customer) : Intent()
        /** User tapped the FAB to add a new customer. */
        data object AddCustomerClicked : Intent()
    }

    /**
     * One-shot side effects that don't belong in State.
     * Navigation and transient UI feedback live here.
     */
    sealed class Effect {
        data class NavigateToCustomerDetail(val customerId: Int) : Effect()
        data object NavigateToAddCustomer : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }
}
