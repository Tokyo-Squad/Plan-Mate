package org.example.presentation.controller.user

import org.example.logic.usecase.user.GetUserByIdUseCase
import org.example.presentation.io.ConsoleIO
import java.util.UUID

class GetUserByIdController(
    private val io: ConsoleIO,
    private val getUserByIdUseCase: GetUserByIdUseCase,
) {
    fun execute() {
        io.printOutput("Enter the ID of the user to retrieve:")
        val idInput = io.readInput("User ID: ")

        if (idInput.isNullOrBlank()) {
            io.printOutput("User ID cannot be empty.")
            return
        }

        val userId: UUID
        try {
            userId = UUID.fromString(idInput)
        } catch (e: IllegalArgumentException) {
            io.printOutput("Invalid User ID format.")
            return
        }

        val result = getUserByIdUseCase(userId)

        result.fold(
            onSuccess = { user ->
                io.printOutput("User Details:")
                io.printOutput("ID: ${user.id}")
                io.printOutput("Username: ${user.username}")
                io.printOutput("Type: ${user.type}")
                // Add other relevant user details here
            },
            onFailure = { exception ->
                io.printOutput("Failed to retrieve user: ${exception.message}")
            }
        )
    }
}