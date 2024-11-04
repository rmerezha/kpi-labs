package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

const val FILE_PATH = "src/main/resources/big.txt"

fun readLines(filePath: String): Flow<String> = flow {
    File(filePath).bufferedReader().useLines { lines ->
        for (line in lines) {
            emit(line)
        }
    }
}

suspend fun countWordInFile(filePath: String, word: String) {
    val lineCounts = LineCounter()

    readLines(filePath)
        .onEach { line ->
            lineCounts.count(line, word)
        }
        .collect()

    printResult(lineCounts)
}

data class LineCounter(var totalLines: Int = 0, var matchingLines: Int = 0) {

    fun count(line: String, word: String) {
        this.totalLines++
        if (line.contains(word)) {
            this.matchingLines++
        }
    }

}

fun printResult(lineCounts: LineCounter) {
    println("Total lines: ${lineCounts.totalLines}")
    println("Word match: ${lineCounts.matchingLines}")
}

fun main() = runBlocking {
    println("Counting word occurrences...")
    val startTime = System.currentTimeMillis()
    countWordInFile(FILE_PATH, "book")
    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime
    println("\nTime: $elapsedTime ms")
}
