package org.example.presentation.controller.user

import org.example.entity.UserEntity
import org.example.logic.usecase.user.DeleteUserUseCase
import org.example.presentation.io.ConsoleIO
import java.util.UUID

class DeleteUserController(
    private val io: ConsoleIO,
    private val deleteUserUseCase: DeleteUserUseCase
) {
    fun execute(currentUser: UserEntity) {
        io.printOutput("Enter the ID of the user to delete:")
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

        val result = deleteUserUseCase(userId, currentUser)

        result.fold(
            onSuccess = {
                io.printOutput("User with ID '$userId' deleted successfully.")
            },
            onFailure = { exception ->
                io.printOutput("Failed to delete user: ${exception.message}")
            }
        )
    }
}