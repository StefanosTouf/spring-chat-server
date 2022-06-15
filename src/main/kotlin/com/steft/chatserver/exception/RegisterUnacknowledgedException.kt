package com.steft.chatserver.exception

data class RegisterUnacknowledgedException(override val message: String)
    : Exception()
