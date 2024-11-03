package org.example

import kotlinx.coroutines.*
import kotlin.system.exitProcess

fun main() {
    val list = mutableListOf<Int>()

    for (i in 0 until 100) {
        list.add(i)
    }
    val startTime = System.currentTimeMillis()

    runBlocking {
        println(asyncMap(list, ::testFunc))
    }

    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime
    println("\nTime: $elapsedTime ms")
}

suspend fun <T, R> asyncMap(
    list: List<T>,
    mapFunction: suspend(T) -> Result<R>,
    handler: (Result<R>) -> R = { it.getOrElse({err ->
        println("Error: ${err.message}")
        exitProcess(0)
    }) }
): List<R> {
    return coroutineScope {
        list.map {
            async { mapFunction(it) }
        }.awaitAll().map { e -> handler(e) }
    }
}

suspend fun testFunc(e: Int): Result<Int> {
    delay(1000)
    return if ((1..100).random() != 1) {
        Result.success(e * e)
    } else {
        Result.failure(Exception("<Error message>"))
    }
}

