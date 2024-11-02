package org.example

import kotlinx.coroutines.*

fun main() {
    val list = mutableListOf<String>()

    for (i in 0 until 1000000) {
        list.add(i.toString())
    }
    val startTime = System.currentTimeMillis()

    runBlocking {
        asyncMap(list, ::testFunc)
        list.add("tree")
        asyncMap(list, ::testFunc)
    }

    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime
    println("\nTime: $elapsedTime ms")
}


fun CoroutineScope.setTimeout(delayMillis: Long, action: () -> Unit): Job {
    return launch {
        delay(delayMillis)
        action()
    }
}

fun CoroutineScope.testFunc(str: String, function: (Result<Int>) -> Unit) {
    setTimeout(1000) {
        try {
            function(Result.success(str.toInt() + 10))
        } catch (e: NumberFormatException) {
            function(Result.failure(e))
        }
    }
}

fun <T, R> asyncMap(
    list: List<T>,
    callback: (T, (Result<R>) -> Unit) -> Unit,
    finalCallback: (Result<List<R>>) -> Unit = {
        it.onSuccess { v -> println("Success: $v") }
            .onFailure { e -> println("Error: $e") }
    }
) {
    var length = list.size
    val resultList: MutableList<R> = mutableListOf()

    var shouldStop = false

    for (i in list.indices) {
        if (shouldStop) break

        callback(list[i]) {
            if (it.isFailure) {
                finalCallback(Result.failure(Exception("Error")))
                shouldStop = true
                return@callback
            }

            resultList.add(it.getOrThrow())
            length--

            if (length == 0) {
                finalCallback(Result.success(resultList))
            }
        }
    }
}


