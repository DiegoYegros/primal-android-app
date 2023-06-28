package net.primal.android.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.primal.android.auth.AuthRepository
import net.primal.android.auth.login.LoginContract.SideEffect
import net.primal.android.auth.login.LoginContract.UiEvent
import net.primal.android.auth.login.LoginContract.UiState
import net.primal.android.settings.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState(loading = false))
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) {
        _state.getAndUpdate { it.reducer() }
    }

    private val _effect: Channel<SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: SideEffect) = viewModelScope.launch {
        _effect.send(effect)
    }

    private val _event: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    fun setEvent(event: UiEvent) {
        viewModelScope.launch { _event.emit(event) }
    }

    init {
        observeEvents()
    }

    private fun observeEvents() = viewModelScope.launch {
        _event.collect {
            when (it) {
                is UiEvent.LoginEvent -> login(nsec = it.nsec)
            }
        }
    }

    private fun login(nsec: String) = viewModelScope.launch {
        setState { copy(loading = true) }
        val pubkey = authRepository.login(nsec = nsec)
        settingsRepository.fetchAppSettings(pubkey = pubkey)
        setEffect(SideEffect.LoginSuccess(pubkey = pubkey))
        setState { copy(loading = false) }
    }

}