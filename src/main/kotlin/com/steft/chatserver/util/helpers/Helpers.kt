@file:Suppress("NOTHING_TO_INLINE")

package com.steft.chatserver.util.helpers

inline fun <A, B : A> B.widen(): A = this