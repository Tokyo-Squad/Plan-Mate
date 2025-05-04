package org.example.presentation.io

class ConsoleIOImpl : ConsoleIO {
    private companion object {
        const val RED = "\u001B[31m"
        const val RESET = "\u001B[0m"
    }

    override fun read(): String = readlnOrNull()?.trim() ?: ""

    override fun write(message: String) {
        println(message)
    }

    override fun writeError(message: String) {
        println("$RED$message$RESET")
    }
}