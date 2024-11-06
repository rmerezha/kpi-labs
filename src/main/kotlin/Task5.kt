package org.example

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalTime

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
        setTimeout(1000) {
            println("do something [event 1] [${LocalTime.now().second}]")
            // ...
        }
    }
    val job2 = eventEmitter.on {
        setTimeout(1000) {
            println("do something [event 2] [${LocalTime.now().second}]")
            // ...
        }
    }

    setTimeout(1000) {
        eventEmitter.emit(Message("Muhamed1", "Hello"))
        eventEmitter.emit(Message("Muhamed2", "Hello"))
        eventEmitter.emit(Message("Muhamed222", "Hello"))
    }
    setTimeout(2000) { eventEmitter.emit(Message("Muhamed3", "Hello")) }
    setTimeout(3000) { eventEmitter.emit(Message("Muhamed4", "Hello")) }

    setTimeout(5000) {
        eventEmitter.off(job1)
        eventEmitter.off(job2)
        eventEmitter.emit(Message("Muhamed4", "Hello World4"))
    }

}