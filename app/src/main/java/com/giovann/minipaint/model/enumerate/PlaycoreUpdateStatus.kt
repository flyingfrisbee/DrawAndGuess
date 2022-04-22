package com.giovann.minipaint.model.enumerate

sealed class PlaycoreUpdateStatus {
    object Mandatory : PlaycoreUpdateStatus()
    object Optional : PlaycoreUpdateStatus()
    object None : PlaycoreUpdateStatus()
}
