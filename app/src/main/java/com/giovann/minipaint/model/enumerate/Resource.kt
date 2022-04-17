package com.giovann.minipaint.model.enumerate

sealed class Resource<T>(val data: T? = null, val msg: String? = null) {
    class Ok<T>(data: T) : Resource<T>(data = data)
    class Failed<T>(msg: String) : Resource<T>(msg = msg)
    class Empty<T> : Resource<T>()
}
