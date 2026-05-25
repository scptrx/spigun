package com.korbuts.spigun.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korbuts.spigun.data.model.Player
import com.korbuts.spigun.data.model.PlayerRole
import com.korbuts.spigun.data.model.RoundConfig
import com.korbuts.spigun.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class GamePlayUiState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val isRevealed: Boolean = false,
    val assignedTopic: String = "",
    val assignedCategory: String = "",
    val playerRoles: Map<String, PlayerRole> = emptyMap(),
    val isGameInitialized: Boolean = false,
    val roundDurationMinutes: Int = 5,
    val spyKnowsCategory: Boolean = false,

    val remainingTimeSeconds: Long = 0,
    val isTimerRunning: Boolean = false,
    val isTimeUp: Boolean = false,

    val votedPlayerIds: Set<String> = emptySet(),
    val configuredSpyCount: Int = 1,
    val isSecretRevealed: Boolean = false,
    val everyoneIsSpy: Boolean = false,
    val noOneIsSpy: Boolean = false
)

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamePlayUiState())
    val uiState: StateFlow<GamePlayUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        initializeGame()
    }

    private fun initializeGame() {
        viewModelScope.launch {
            val config = repository.roundConfig.first()
            val allGroups = repository.getGroups().first()
            val selectedGroup = allGroups.find { it.id == config.selectedGroupId } ?: return@launch
            val allPacks = repository.getTopicPacks().first()
            val activePacks = allPacks.filter { it.id in config.activeTopicsPackIds }
            
            if (activePacks.isEmpty()) return@launch

            val randomPack = activePacks.random()
            val randomTopic = randomPack.words.random()
            
            val players = selectedGroup.players.shuffled()
            val roles = assignRoles(players, config)

            val everyoneSpy = roles.values.all { it == PlayerRole.SPY }
            val noOneSpy = roles.values.all { it == PlayerRole.PLAYER } && config.surpriseGameEnabled

            _uiState.value = GamePlayUiState(
                players = players,
                assignedTopic = randomTopic,
                assignedCategory = randomPack.name,
                playerRoles = roles,
                isGameInitialized = true,
                roundDurationMinutes = config.roundDurationMinutes,
                spyKnowsCategory = config.spyKnowsCategory,
                remainingTimeSeconds = config.roundDurationMinutes * 60L,
                configuredSpyCount = config.spyCount,
                everyoneIsSpy = everyoneSpy,
                noOneIsSpy = noOneSpy && roles.values.all { it == PlayerRole.PLAYER }
            )
        }
    }

    private fun assignRoles(players: List<Player>, config: RoundConfig): Map<String, PlayerRole> {
        val roles = mutableMapOf<String, PlayerRole>()

        if (config.surpriseGameEnabled && Random.nextInt(100) < 100) {
            val everyoneIsSpy = Random.nextBoolean()
            players.forEach { 
                roles[it.id] = if (everyoneIsSpy) PlayerRole.SPY else PlayerRole.PLAYER 
            }
            return roles
        }

        players.forEach { roles[it.id] = PlayerRole.PLAYER }
        val spyIndices = (players.indices).shuffled().take(config.spyCount)
        spyIndices.forEach { index ->
            roles[players[index].id] = PlayerRole.SPY
        }
        return roles
    }

    fun toggleReveal() {
        _uiState.value = _uiState.value.copy(isRevealed = !_uiState.value.isRevealed)
    }

    fun nextPlayer() {
        val nextIndex = _uiState.value.currentPlayerIndex + 1
        if (nextIndex < _uiState.value.players.size) {
            _uiState.value = _uiState.value.copy(isRevealed = false)
            _uiState.value = _uiState.value.copy(
                currentPlayerIndex = nextIndex
            )
        }
    }

    fun isLastPlayer(): Boolean {
        return _uiState.value.currentPlayerIndex == _uiState.value.players.size - 1
    }

    fun startTimer() {
        if (_uiState.value.isTimerRunning) return
        
        _uiState.value = _uiState.value.copy(isTimerRunning = true)
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTimeSeconds > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    remainingTimeSeconds = _uiState.value.remainingTimeSeconds - 1
                )
            }
            _uiState.value = _uiState.value.copy(
                isTimerRunning = false,
                isTimeUp = true
            )
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerRunning = false)
    }

    fun endRoundEarly() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            remainingTimeSeconds = 0,
            isTimerRunning = false,
            isTimeUp = true
        )
    }

    fun toggleVotedPlayer(playerId: String) {
        val current = _uiState.value.votedPlayerIds
        val newSelection = if (current.contains(playerId)) {
            current - playerId
        } else {
            if (current.size < _uiState.value.configuredSpyCount) {
                current + playerId
            } else {
                current
            }
        }
        _uiState.value = _uiState.value.copy(votedPlayerIds = newSelection)
    }

    fun revealSecret() {
        _uiState.value = _uiState.value.copy(isSecretRevealed = true)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
