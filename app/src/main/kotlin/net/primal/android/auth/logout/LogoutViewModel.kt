package net.primal.android.auth.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.primal.android.auth.AuthRepository
import net.primal.android.auth.logout.LogoutContract.UiEvent
import net.primal.android.db.PrimalDatabase
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val database: PrimalDatabase,
) : ViewModel() {

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
                UiEvent.LogoutConfirmed -> logout()
            }
        }
    }

    private suspend fun logout() {
        withContext(Dispatchers.IO) { database.clearAllTables() }
        authRepository.logout()
    }

}