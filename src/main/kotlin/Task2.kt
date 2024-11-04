package org.example

import kotlinx.coroutines.*

fun main() = runBlocking {
    val list1 = listOf(0.0, 4.0, 9.0, 25.0, 36.0, 49.0)
    val list2 = listOf(0.0, -1.0, 2.0, 3.0, 4.0, 5.0)

    val startTime = System.currentTimeMillis()

    val job1 = launch { println(asyncMap(list1, ::sqrt)) }
    val job2 = launch { try { println(asyncMap(list2, ::sqrt))} catch (e: Exception) {println(e.message)} }

    job1.join()
    job2.join()

    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime
    println("\nTime: $elapsedTime ms")
}

suspend fun <T, R> asyncMap(
    items: List<T>,
    asyncTransform: suspend (T) -> Result<R>,
    resultHandler: (Result<R>) -> R = { it.getOrThrow() }
): List<R> {
    return coroutineScope {
        items.map { item ->
            async { asyncTransform(item) }
        }.awaitAll().map(resultHandler)
    }
}

suspend fun sqrt(num: Double): Result<Double> {
    delay(1000)
    if (num < 0) {
        return Result.failure(Exception("negative value: $num"))
    }
    return Result.success(kotlin.math.sqrt(num))

}

