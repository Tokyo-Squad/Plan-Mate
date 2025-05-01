package org.example.presentation.controller.user

import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUsersUseCase
import org.example.presentation.io.ConsoleIO

class GetUsersController(
    private val io: ConsoleIO,
    private val getUsersUseCase: GetUsersUseCase
) {
    fun execute() {
        val result = getUsersUseCase()

        result.fold(
            onSuccess = { users ->
                if (users.isEmpty()) {
                    io.printOutput("No users found.")
                } else {
                    io.printOutput("List of Users:")
                    users.forEach { user ->
                        io.printOutput("  ID: ${user.id}")
                        io.printOutput("  Username: ${user.username}")
                        io.printOutput("  Type: ${user.type}")
                        io.printOutput("---")
                    }
                }
            },
            onFailure = { exception ->
                io.printOutput("Failed to retrieve users: ${exception.message}")
            }
        )
    }
}