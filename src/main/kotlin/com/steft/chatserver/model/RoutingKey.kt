package com.steft.chatserver.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RoutingKey(val string: String)
