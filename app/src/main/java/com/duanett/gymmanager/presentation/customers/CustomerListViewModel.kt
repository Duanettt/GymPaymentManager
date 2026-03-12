package com.duanett.gymmanager.presentation.customers

import androidx.lifecycle.viewModelScope
import com.duanett.gymmanager.data.repository.CustomerRepository
import com.duanett.gymmanager.domain.model.Customer
import com.duanett.gymmanager.presentation.customers.CustomerListContract.Effect
import com.duanett.gymmanager.presentation.customers.CustomerListContract.Intent
import com.duanett.gymmanager.presentation.customers.CustomerListContract.State
import com.duanett.gymmanager.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @HiltViewModel tells Hilt to generate a factory for this ViewModel
 * so it can be injected with hiltViewModel() in Composables.
 *
 * @Inject on the constructor is how Hilt knows what to provide —
 * CustomerRepository is resolved from the DI graph automatically.
 *
 * Note: individual imports from CustomerListContract instead of a wildcard,
 * because Kotlin does not allow wildcard imports from objects (import obj.*).
 */
@HiltViewModel
@OptIn(FlowPreview::class)
class CustomerListViewModel @Inject constructor(
    private val repository: CustomerRepository
) : MviViewModel<CustomerListContract.State, CustomerListContract.Intent, CustomerListContract.Effect>(State()) {

    private val searchQuery = MutableStateFlow("")

    init {
        observeCustomers()
        observeSearchQuery()
    }

    private fun observeCustomers() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            repository.getAllCustomers()
                .catch { e -> setState { copy(isLoading = false, error = e.message) } }
                .collect { customers ->
                    setState { copy(customers = customers, isLoading = false, error = null) }
                }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val flow = if (query.isBlank()) repository.getAllCustomers()
                               else repository.searchCustomers(query)
                    flow.catch { e -> setState { copy(error = e.message) } }
                        .collect { customers -> setState { copy(customers = customers) } }
                }
        }
    }

    override fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadCustomers      -> observeCustomers()
            is Intent.SearchCustomers    -> onSearch(intent.query)
            is Intent.DeleteCustomer     -> onDeleteCustomer(intent.customer)
            is Intent.ToggleActiveStatus -> onToggleActive(intent.customer)
            is Intent.SelectCustomer     -> sendEffect(Effect.NavigateToCustomerDetail(intent.customer.id))
            is Intent.AddCustomerClicked -> sendEffect(Effect.NavigateToAddCustomer)
        }
    }

    private fun onSearch(query: String) {
        setState { copy(searchQuery = query) }
        searchQuery.value = query
    }

    private fun onDeleteCustomer(customer: Customer) {
        viewModelScope.launch {
            runCatching { repository.deleteCustomer(customer) }
                .onSuccess { sendEffect(Effect.ShowSnackbar("${customer.name} removed")) }
                .onFailure { sendEffect(Effect.ShowSnackbar("Failed to delete ${customer.name}")) }
        }
    }

    private fun onToggleActive(customer: Customer) {
        viewModelScope.launch {
            runCatching { repository.updateCustomer(customer.copy(isActive = !customer.isActive)) }
                .onFailure { sendEffect(Effect.ShowSnackbar("Failed to update ${customer.name}")) }
        }
    }
}
