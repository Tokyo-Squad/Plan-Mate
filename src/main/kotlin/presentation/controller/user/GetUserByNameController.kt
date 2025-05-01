package org.example.presentation.controller.user

import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUserByIdUseCase
import org.example.logic.usecase.user.GetUserByUsernameUseCase
import org.example.presentation.io.ConsoleIO

class GetUserByNameController(
    private val io: ConsoleIO,
    private val getUserByUsernameUseCase: GetUserByUsernameUseCase
) {

    fun execute() {
        io.printOutput("Enter the username of the user to retrieve:")
        val usernameInput = io.readInput("Username: ")

        if (usernameInput.isNullOrBlank()) {
            io.printOutput("Username cannot be empty.")
            return
        }

        val result = getUserByUsernameUseCase(usernameInput)

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