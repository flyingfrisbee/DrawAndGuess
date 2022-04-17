package com.giovann.minipaint.repository

import com.giovann.minipaint.api.ScribblerAPI
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.model.response.ScribblerResponse

class ScribblerRepoImpl(
    private val api: ScribblerAPI
) : ScribblerRepo {

    override suspend fun createRoom(roomName: String): Resource<ScribblerResponse> {
        return try {
            val response = api.createRoom(roomName)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Ok(result!!)
            } else {
                Resource.Failed("Internal server error")
            }
        } catch (e: Exception) {
            return Resource.Failed(e.message ?: "No internet connection")
        }
    }

    override suspend fun joinRoom(roomName: String): Resource<ScribblerResponse> {
        return try {
            val response = api.joinRoom(roomName)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Ok(result!!)
            } else {
                Resource.Failed("Internal server error")
            }
        } catch (e: Exception) {
            return Resource.Failed(e.message ?: "No internet connection")
        }
    }
}