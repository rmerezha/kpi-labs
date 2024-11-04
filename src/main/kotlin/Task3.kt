package org.example

import kotlinx.coroutines.*

class TaskProcessor {
    private val job = Job();
    private val scope = CoroutineScope(Dispatchers.Default + job);

    suspend fun <T, R> asyncMap(
        items: List<T>,
        asyncTransform: suspend (T) -> Result<R>,
        resultHandler: (Result<R>) -> R = { it.getOrThrow() },
        onCancellation: () -> Unit = { println("Job was cancelled") }
    ): List<R>? {
        try {
            return coroutineScope {
                items.map {
                    scope.async { asyncTransform(it) }
                }.awaitAll().map(resultHandler)
            }
        } catch (e: CancellationException) {
            onCancellation()
            return null
        }
    }

    fun cancel() {
        job.cancel()
    }
}


fun main() {
    runBlocking {
        val tp = TaskProcessor()
        val list1: List<Int> = listOf(1, 2, 3, 4, 5)
        val list2: List<Int> = listOf(1, -2, 3, 4, 5)

        val startTime = System.currentTimeMillis()
        launch { println(tp.asyncMap(list1, ::sqrt)) }
        launch { try { println(tp.asyncMap(list2, ::sqrt))} catch (e: IllegalArgumentException) {println(e.message)} }
        setTimeout(500) {
            tp.cancel()
            val endTime = System.currentTimeMillis()
            val elapsedTime = endTime - startTime
            println("\nTime: $elapsedTime ms")
        }
    }
}

suspend fun sqrt(num: Int): Result<Int> {
    delay(1000)
    return if (num < 0) {
        Result.failure(IllegalArgumentException("negative value: $num"))
    } else {
        Result.success(num * num * num)
    }
}