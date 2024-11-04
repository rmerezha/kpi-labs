package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class Message(val sender: String, val content: String)

class EventEmitter {
    private val events = MutableSharedFlow<Message>()

    suspend fun emit(message: Message) {
        events.emit(message)
    }

    fun on(action: suspend (Message) -> Unit): Job {
        return GlobalScope.launch {
            events.collect { message ->
                action(message)
            }
        }
    }

    fun off(job: Job) {
        job.cancel()
    }
}

fun main(): Unit = runBlocking {

    val eventEmitter = EventEmitter()
    val job1 = eventEmitter.on {
        println("event 1: $it")

    }
    val job2 = eventEmitter.on {
        println("event 2: $it")
    }

    setTimeout(1000) { eventEmitter.emit(Message("Muhamed1", "Hello World1")) }
    setTimeout(2000) { eventEmitter.emit(Message("Muhamed2", "Hello World2")) }
    setTimeout(3000) { eventEmitter.emit(Message("Muhamed3", "Hello World3")) }

    setTimeout(5000) {
        eventEmitter.off(job1)
        eventEmitter.off(job2)
        eventEmitter.emit(Message("Muhamed4", "Hello World4"))
    }

}