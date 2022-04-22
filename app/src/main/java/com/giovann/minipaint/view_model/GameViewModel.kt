package com.giovann.minipaint.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.model.game.GameStatusUpdate
import com.giovann.minipaint.model.response.ScribblerResponse
import com.giovann.minipaint.repository.ScribblerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repo: ScribblerRepo
) : ViewModel() {
    var playerUID = -1
    var currentTurn = -2
    var heightPixel = 0
    var widthPixel = 0
    var isDrawingTurn = false

    private val _gameStatusUpdate = MutableLiveData<GameStatusUpdate>()
    val gameStatusUpdate: LiveData<GameStatusUpdate> = _gameStatusUpdate

    fun updateStatus(stat: GameStatusUpdate) = viewModelScope.launch {
        _gameStatusUpdate.value = stat
    }

    private val _closeGameSignal = MutableLiveData<Boolean>()
    val closeGameSignal: LiveData<Boolean> = _closeGameSignal

    fun sendCloseGameSignal() = viewModelScope.launch {
        _closeGameSignal.value = true
    }
}