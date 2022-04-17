package com.giovann.minipaint.repository

import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.model.response.ScribblerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScribblerRepo {

    suspend fun createRoom(
        roomName: String
    ): Resource<ScribblerResponse>

    suspend fun joinRoom(
        roomName: String
    ): Resource<ScribblerResponse>
}