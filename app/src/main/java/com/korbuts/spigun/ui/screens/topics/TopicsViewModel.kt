package com.korbuts.spigun.ui.screens.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korbuts.spigun.data.model.MockData
import com.korbuts.spigun.data.model.TopicsPack
import com.korbuts.spigun.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TopicsUiState(
    val customPacks: List<TopicsPack> = emptyList(),
    val defaultPacks: List<TopicsPack> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TopicsUiState> = combine(
        repository.getTopicPacks(),
        _searchQuery
    ) { packs, query ->
        val filteredPacks = if (query.isBlank()) {
            packs
        } else {
            packs.filter { it.name.contains(query, ignoreCase = true) }
        }
        
        TopicsUiState(
            customPacks = filteredPacks.filter { it.isCustom },
            defaultPacks = filteredPacks.filter { !it.isCustom },
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TopicsUiState()
    )

    init {
        seedDefaultTopics()
    }

    private fun seedDefaultTopics() {
        viewModelScope.launch {
            val currentPacks = repository.getTopicPacks().first()
            if (currentPacks.isEmpty()) {
                MockData.defaultTopicPacks.forEach {
                    repository.saveTopicPack(it)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addTopicPack(name: String, words: List<String>) {
        if (name.isBlank() || words.size < 5) return
        viewModelScope.launch {
            repository.saveTopicPack(
                TopicsPack(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    words = words,
                    isCustom = true
                )
            )
        }
    }

    fun editTopicPack(pack: TopicsPack) {
        if (pack.name.isBlank() || pack.words.size < 5) return
        viewModelScope.launch {
            repository.saveTopicPack(pack)
        }
    }

    fun deleteTopicPack(packId: String) {
        viewModelScope.launch {
            repository.deleteTopicPack(packId)
        }
    }
}
