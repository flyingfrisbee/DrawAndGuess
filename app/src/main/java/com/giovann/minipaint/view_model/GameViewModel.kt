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


}