package com.giovann.minipaint.api

import com.giovann.minipaint.model.response.ScribblerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScribblerAPI {

    @GET("/createroom/{room_name}")
    suspend fun createRoom(
        @Path("room_name") roomName: String
    ): Response<ScribblerResponse>

    @GET("/joinroom/{room_name}")
    suspend fun joinRoom(
        @Path("room_name") roomName: String
    ): Response<ScribblerResponse>
}