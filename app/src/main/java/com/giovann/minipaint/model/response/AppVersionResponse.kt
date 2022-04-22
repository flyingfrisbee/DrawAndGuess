package com.giovann.minipaint.model.response


import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("mandatory_version")
    val mandatoryVersion: Int,
    @SerializedName("optional_version")
    val optionalVersion: Int
)