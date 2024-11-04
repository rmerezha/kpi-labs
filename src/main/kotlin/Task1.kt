package org.example

import kotlinx.coroutines.*

fun main() = runBlocking {

    val list1 = listOf("1", "2", "3", "4");
    val list2 = listOf("1", "two", "3", "4");


    val startTime = System.currentTimeMillis()

    async {
        asyncMap(list1, ::strToInt)
        asyncMap(list2, ::strToInt)
    }.await()

    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime
    println("\nTime: $elapsedTime ms")
}

fun <T, R> asyncMap(
    items: List<T>,
    asyncTask: (T, (Result<R>) -> Unit) -> Unit, // (err, ...) => {...}
    onComplete: (Result<List<R>>) -> Unit = { result ->
        result
            .onSuccess { values -> println("Success: $values") }
            .onFailure { error -> println("Error: ${error.message}") }
    }
) {
    var pendingTasks = items.size
    val results: MutableList<R> = mutableListOf()
    var hasErrorOccurred = false

    for (item in items) {
        if (hasErrorOccurred) break

        asyncTask(item) { result ->
            if (result.isFailure) {
                onComplete(Result.failure(Exception(result.exceptionOrNull()?.message)))
                hasErrorOccurred = true
                return@asyncTask
            }

            result.onSuccess { value ->
                results.add(value)
                pendingTasks--

                if (pendingTasks == 0) {
                    onComplete(Result.success(results))
                }
            }
        }
    }
}


fun CoroutineScope.setTimeout(delayMillis: Long, action: suspend () -> Unit): Job {
    return launch {
        delay(delayMillis)
        action()
    }
}

fun CoroutineScope.strToInt(str: String, function: (Result<Int>) -> Unit) {
    setTimeout(1000) {
        try {
            function(Result.success(str.toInt()))
        } catch (e: NumberFormatException) {
            function(Result.failure(e))
        }
    }
}




