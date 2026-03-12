package com.duanett.gymmanager.presentation.mvi

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Base ViewModel that enforces the MVI contract.
 *
 * MVI (Model-View-Intent) data flow:
 *
 *   ┌──────────────────────────────────────────────────────┐
 *   │                                                      │
 *   │  View ──Intent──► ViewModel ──State──► View          │
 *   │                       │                              │
 *   │                       └──Effect──► View (one-shot)   │
 *   │                                                      │
 *   └──────────────────────────────────────────────────────┘
 *
 * - [State]  : Immutable snapshot of everything the UI needs to render.
 *              Backed by StateFlow so new collectors immediately get the latest value.
 * - [Intent] : A user action or event (button tap, text input, lifecycle event).
 *              The View never mutates state directly — it sends an Intent.
 * - [Effect] : One-shot side effects that shouldn't live in State
 *              (navigation, snackbars, toasts). Backed by Channel so they
 *              are delivered exactly once even if the collector is slow.
 *
 * @param S State type
 * @param I Intent type
 * @param E Effect type
 */

abstract class MviViewModel<S, I, E>(initialState: S) : ViewModel() {
    // Base state manipulation
    private val _state = MutableStateFlow<S>(initialState)
    val state = _state.asStateFlow()

    // One time effects
    // Channel.BUFFERED keeps effects queued if the collector isn't ready yet.
    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // Handling our intents, UI calls this when the user does an action
    fun handleIntent(intent: I) {
        onIntent(intent)
    }

    // Subclasses e.g other view models implement this and define the definitions for the actions
    protected abstract fun onIntent(intent: I)

    // Updates the current UI state by applying a reducer function to the existing state
    // and emitting the resulting new state.
    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    /** Send a one-shot effect to the UI. Safe to call from any coroutine. */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
