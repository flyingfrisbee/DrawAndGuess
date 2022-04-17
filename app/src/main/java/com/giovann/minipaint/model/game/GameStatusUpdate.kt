package com.giovann.minipaint.model.game


import com.google.gson.annotations.SerializedName

data class GameStatusUpdate(
    @SerializedName("Answer")
    val answer: String,
    @SerializedName("CurrentlyDrawing")
    val currentlyDrawing: Int,
    @SerializedName("Players")
    val players: List<Player>
)

data class Player(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Score")
    val score: Int,
    @SerializedName("UID")
    val UID: Int
)