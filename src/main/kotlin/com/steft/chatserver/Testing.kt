@file:Suppress("NAME_SHADOWING")

package com.steft.chatserver

import com.steft.chatserver.util.helpers.widen
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.Duration
import com.steft.chatserver.util.helpers.widen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.reactor.mono


fun factory(i: Int) =
    Flux.just(i, i)

val conns = mapOf(1 to true, 3 to true, 4 to true)

fun send(i: Int): Mono<Boolean> =
    Mono.just(conns[i] ?: false)


//fun main() = runBlocking {
//    Flux.just(1, 2, 1, 4, 2)
//        .groupBy { it }
//        .flatMap { g ->
//            g.flatMap {
//                send(it)
//                    .map { 1 to it }
//                    .expand({ (repeats, previous) ->
//                        if (previous)
//                            Mono.just(println("Sent $it"))
//                                .then(Mono.empty())
//                        else
//                            Mono.just(it)
//                                .delayElement(Duration.ofMillis(500))
//                                .doOnNext { println("Retrying $it \n attempts: $repeats") }
//                                .flatMap { send(it).map { repeats + 1 to it } }
//                    }, 3)
//            }
//        }
//        .subscribe()
//
//    delay(1000000)
//}

//val sender: Sender = TODO()
//val outboundMessage: Mono<OutboundMessage> = TODO()
//
//fun main() = runBlocking {
//
////    sender.sendWithPublishConfirms(outboundMessage).expand { a ->
////        sender.sendWithPublishConfirms(a.outboundMessage)
////    }
//
//    flux {
//        while (true) {
//            delay(10); send(Random.nextInt(3))
//        }
//    }
//        .groupBy { it }
//        .flatMap { g ->
//            Mono.just(1)
//                .doOnNext { println("First") }
//                .thenMany(g)
//                .flatMap {
////                    sender.sendWithPublishConfirms()
//                    mono { it }
//                }
//                .timeout(Duration.ofMillis(10000), Mono.empty())
//                .doOnComplete { println("Cancelled group ${g.key()}") }
//        }
//        .doOnNext { println(it) }
//        .subscribe()
//
//    delay(1000000)
//}

//
//val shared =
//    Flux.range(1, 10000)
//        .delayElements(Duration.ofMillis(1000))
//        .share()
//
//val qs = mapOf(
//    1 to Flux.just(1).delayElements(Duration.ofMillis(10)).repeat(),
//    2 to Flux.just(2).delayElements(Duration.ofMillis(10)).repeat(),
//    3 to Flux.just(3).delayElements(Duration.ofMillis(10)).repeat(20),
//    4 to Flux.just(4).delayElements(Duration.ofMillis(10)).repeat(),
//    5 to Flux.just(5).delayElements(Duration.ofMillis(10)).repeat(20))
//
//val a = Flux.create<Int> { sink ->
//    sink.next(1)
//        .next(2)
//        .next(3)
//}
//val receiveFrom =
//    Flux.range(1, 5)
//        .delayElements(Duration.ofMillis(2000))
//        .flatMap { qs[it]!! }
//        .doOnNext(::println)
//
//fun doWithSink(sink: Sinks.Many<Int>) = sink.apply {
//    tryEmitNext(1)
//    tryEmitNext(2)
//    tryEmitNext(3)
//    tryEmitNext(4)
//    tryEmitNext(5)
//}.asFlux()
//

typealias Message = Pair<Id, String>
typealias Id = Int

sealed interface RouterEvents {
    @JvmInline
    value class NewMessage(val message: Message) : RouterEvents

    @JvmInline
    value class Listen(val listen: Pair<Id, Sinks.Many<Message>>) : RouterEvents
}

fun newMessage(message: Message) = RouterEvents.NewMessage(message)
fun listen(listen: Pair<Id, Sinks.Many<Message>>) = RouterEvents.Listen(listen)

object MessageRouter {
    private val listenEvents: Sinks.Many<Pair<Id, Sinks.Many<Message>>> =
        Sinks.many()
            .unicast()
            .onBackpressureBuffer()

    fun listen(id: Id): Flux<Message> =
        Sinks.many()
            .unicast()
            .onBackpressureBuffer<Message>()
            .also { messages ->
                Sinks.EmitFailureHandler
                    .busyLooping(Duration.ofMillis(250))
                    .let { failHandler ->
                        listenEvents.emitNext(id to messages, failHandler)
                    }
            }.asFlux()

    fun init(messages: Flux<Message>) =
        messages
            .map(::newMessage)
            .map { it.widen<RouterEvents, RouterEvents.NewMessage>() }
            .mergeWith(
                listenEvents.asFlux()
                    .map(::listen))
            .reduce(HashMap<Id, Sinks.Many<Message>>()) { map, event ->
                when (event) {
                    is RouterEvents.NewMessage ->
                        map[event.message.first]
                            ?.emitNext(event.message, Sinks.EmitFailureHandler.FAIL_FAST)
                            ?: println("Dropping message $event")
                    is RouterEvents.Listen ->
                        map[event.listen.first] = event.listen.second
                }
                map
            }
}

val incomingMessages: Flux<Message> =
    flux {
        send(1 to "Hello 1")
        send(1 to "Hello 1")
        send(2 to "Hello 2")
        send(1 to "Hello 1")
        send(4 to "Hello 4")
        send(5 to "Hello 5")
    }
        .delayElements(
            Duration.ofMillis(500))
        .repeat()

//fun main() = runBlocking {
//    MessageRouter
//        .init(incomingMessages)
//        .doOnError { println(it) }
//        .subscribe()
//
//    MessageRouter
//        .listen(1)
//        .doOnNext { println("Client 1: $it") }
//        .take(Duration.ofMillis(2000))
//        .doOnError { println(it) }
//        .doOnComplete { println("Completed") }
//        .subscribe()
//
//    MessageRouter
//        .listen(2)
//        .doOnNext { println("Client 2: $it") }
//        .subscribe()
//
//    MessageRouter
//        .listen(5)
//        .doOnNext { println("Client 5: $it") }
//        .subscribe()
//
//    delay(100000000)
//}
