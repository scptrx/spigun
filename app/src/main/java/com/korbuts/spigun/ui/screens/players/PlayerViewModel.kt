package com.korbuts.spigun.ui.screens.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korbuts.spigun.data.model.Player
import com.korbuts.spigun.data.model.PlayerGroup
import com.korbuts.spigun.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val groups: List<PlayerGroup> = emptyList(),
    val selectedPlayerIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _selectedPlayerIds = MutableStateFlow<Set<String>>(emptySet())
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<PlayerUiState> = combine(
        repository.getAllPlayers(),
        repository.getGroups(),
        _selectedPlayerIds,
        _searchQuery
    ) { players, groups, selectedIds, query ->
        PlayerUiState(
            players = players.filter { it.name.contains(query, ignoreCase = true) },
            groups = groups,
            selectedPlayerIds = selectedIds,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun togglePlayerSelection(playerId: String) {
        _selectedPlayerIds.value = if (_selectedPlayerIds.value.contains(playerId)) {
            _selectedPlayerIds.value - playerId
        } else {
            _selectedPlayerIds.value + playerId
        }
    }

    fun addPlayer(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.savePlayer(Player(id = UUID.randomUUID().toString(), name = name))
        }
    }

    fun editPlayer(player: Player) {
        if (player.name.isBlank()) return
        viewModelScope.launch {
            repository.savePlayer(player)
        }
    }

    fun deletePlayer(playerId: String) {
        viewModelScope.launch {
            repository.deletePlayer(playerId)
            if (_selectedPlayerIds.value.contains(playerId)) {
                _selectedPlayerIds.value -= playerId
            }
        }
    }

    fun deletePlayers() {
        val selectedIds = _selectedPlayerIds.value.toList()
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            repository.deletePlayers(selectedIds)
            _selectedPlayerIds.value = emptySet()
        }
    }

    fun getCriticalGroupsForPlayer(playerId: String): List<PlayerGroup> {
        return uiState.value.groups.filter { group ->
            group.players.any { it.id == playerId } && group.players.size <= 3
        }
    }

    fun createGroup(name: String?) {
        val selectedPlayers = uiState.value.players.filter { it.id in _selectedPlayerIds.value }
        if (selectedPlayers.isEmpty()) return

        val finalName = if (name.isNullOrBlank()) {
            selectedPlayers.joinToString(", ") { it.name }
        } else {
            name
        }

        viewModelScope.launch {
            repository.saveGroup(
                PlayerGroup(
                    id = UUID.randomUUID().toString(),
                    name = finalName,
                    players = selectedPlayers
                )
            )
            _selectedPlayerIds.value = emptySet()
        }
    }


    fun editGroup(group: PlayerGroup) {
        if (group.name.isBlank()) return
        viewModelScope.launch {
            repository.saveGroup(group)
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteGroup(groupId)
        }
    }
}
