package com.example.friendupp.model

class SocialException(override val message: String, e: Exception) : Exception(e)

sealed class Response<out T>{


    object Loading: Response<Nothing>()

    data class Success <out T>(val data: T):Response<T>()

    data class Failure (val e : SocialException):Response<Nothing>()

}
