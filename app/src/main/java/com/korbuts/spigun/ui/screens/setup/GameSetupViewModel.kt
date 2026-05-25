package com.korbuts.spigun.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korbuts.spigun.data.model.PlayerGroup
import com.korbuts.spigun.data.model.RoundConfig
import com.korbuts.spigun.data.model.TopicsPack
import com.korbuts.spigun.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameSetupUiState(
    val groups: List<PlayerGroup> = emptyList(),
    val topicPacks: List<TopicsPack> = emptyList(),
    val roundConfig: RoundConfig = RoundConfig(),
    val isLoading: Boolean = true
)

@HiltViewModel
class GameSetupViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<GameSetupUiState> = combine(
        repository.getGroups(),
        repository.getTopicPacks(),
        repository.roundConfig
    ) { groups, topics, config ->
        GameSetupUiState(
            groups = groups,
            topicPacks = topics,
            roundConfig = config,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameSetupUiState()
    )

    fun updateRoundDuration(minutes: Int) {
        viewModelScope.launch {
            repository.updateRoundConfig { it.copy(roundDurationMinutes = minutes) }
        }
    }

    fun updateSpyCount(count: Int) {
        viewModelScope.launch {
            val group = uiState.value.groups.find { it.id == uiState.value.roundConfig.selectedGroupId }
            val playerCount = group?.players?.size ?: 5
            val maxAllowed = (playerCount - 1).coerceAtLeast(1)
            
            repository.updateRoundConfig { it.copy(spyCount = count.coerceAtMost(maxAllowed)) }
        }
    }

    fun toggleTopicPack(packId: String) {
        viewModelScope.launch {
            repository.updateRoundConfig { current ->
                val newTopics = if (current.activeTopicsPackIds.contains(packId)) {
                    current.activeTopicsPackIds - packId
                } else {
                    current.activeTopicsPackIds + packId
                }
                current.copy(activeTopicsPackIds = newTopics)
            }
        }
    }

    fun selectGroup(groupId: String) {
        viewModelScope.launch {
            val group = uiState.value.groups.find { it.id == groupId }
            val playerCount = group?.players?.size ?: 0
            val maxAllowed = (playerCount - 1).coerceAtLeast(1)
            
            repository.updateRoundConfig { current ->
                val newSpyCount = current.spyCount.coerceAtMost(maxAllowed)
                current.copy(
                    selectedGroupId = groupId,
                    spyCount = newSpyCount
                )
            }
        }
    }

    fun toggleSpyKnowsCategory() {
        viewModelScope.launch {
            repository.updateRoundConfig { it.copy(spyKnowsCategory = !it.spyKnowsCategory) }
        }
    }

    fun toggleSurpriseGame() {
        viewModelScope.launch {
            repository.updateRoundConfig { it.copy(surpriseGameEnabled = !it.surpriseGameEnabled) }
        }
    }
}
