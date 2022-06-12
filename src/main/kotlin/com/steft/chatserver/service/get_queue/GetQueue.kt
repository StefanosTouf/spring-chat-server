package com.steft.chatserver.service.get_queue

import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.model.UserId

interface GetQueue: (UserId) -> RabbitQueue