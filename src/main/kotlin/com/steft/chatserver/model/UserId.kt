package com.steft.chatserver.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class UserId(val string: String)
