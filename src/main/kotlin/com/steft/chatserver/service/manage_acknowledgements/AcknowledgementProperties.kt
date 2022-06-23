package com.steft.chatserver.service.manage_acknowledgements

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("acknowledgement")
data class AcknowledgementProperties(
    val retries: Int,
    val interval: Duration,
    val nonSerializedBusyLooping: Duration)