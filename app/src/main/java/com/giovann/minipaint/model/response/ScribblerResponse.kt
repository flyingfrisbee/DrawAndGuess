package com.giovann.minipaint.model.response


import com.google.gson.annotations.SerializedName

data class ScribblerResponse(
    @SerializedName("Success")
    val success: Boolean
)