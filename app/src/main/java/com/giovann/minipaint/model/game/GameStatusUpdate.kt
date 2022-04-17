package com.giovann.minipaint.model.game


import com.google.gson.annotations.SerializedName

data class GameStatusUpdate(
    val answer: String,
    @SerializedName("currently_drawing")
    val currentlyDrawing: Int,
    val players: List<Player>
)

data class Player(
    @SerializedName("has_answered")
    val hasAnswered: Boolean,
    val name: String,
    val score: Int,
    val uid: Int
)