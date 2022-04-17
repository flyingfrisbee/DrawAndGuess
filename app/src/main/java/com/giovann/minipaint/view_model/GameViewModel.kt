package com.giovann.minipaint.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.model.response.ScribblerResponse
import com.giovann.minipaint.repository.ScribblerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repo: ScribblerRepo
) : ViewModel() {

    private val _createRoomResp = MutableLiveData<Resource<ScribblerResponse>>()
    val createRoomResp: LiveData<Resource<ScribblerResponse>> = _createRoomResp

    fun executeCreateRoom(roomName: String) = viewModelScope.launch {
        _createRoomResp.value = repo.createRoom(roomName)
        _createRoomResp.value = Resource.Empty()
    }

    private val _joinRoomResp = MutableLiveData<Resource<ScribblerResponse>>()
    val joinRoomResp: LiveData<Resource<ScribblerResponse>> = _joinRoomResp

    fun executeJoinRoom(roomName: String) = viewModelScope.launch {
        _joinRoomResp.value = repo.joinRoom(roomName)
        _joinRoomResp.value = Resource.Empty()
    }
}