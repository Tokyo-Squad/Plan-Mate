package org.example.presentation

import AdminScreen
import MateScreen
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.auth.LoginUseCase
import org.example.logic.usecase.auth.LogoutUseCase
import org.example.presentation.io.ConsoleIO
import kotlin.system.exitProcess

class PlanMateConsoleUI(
    private val console: ConsoleIO,
    private val loginUseCase: LoginUseCase,
    private val adminScreen: AdminScreen,
    private val mateScreen: MateScreen,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) {
    private var currentUser: UserEntity? = null

    fun start() {
        currentUser = getCurrentUserUseCase.invoke().getOrNull()


        while (true) {
            try {
                showWelcome()
                val option = showMainMenu()

                if (currentUser != null) {
                    when (option) {
                        1 -> routeToUserScreen(currentUser!!)
                        2 -> logout()
                        else -> console.writeError("Invalid option. Please try again.")
                    }
                } else {
                    when (option) {
                        1 -> handleLogin()
                        2 -> exit()
                        else -> console.writeError("Invalid option. Please try again.")
                    }
                }
            } catch (e: Exception) {
                console.writeError("An error occurred: ${e.message}")
                console.write("\nPress Enter to continue...")
                console.read()
            }
        }
    }

    private fun showWelcome() {
        console.write("\n=== Welcome to PlanMate v${VERSION} ===")
        console.write("A Simple Task Management System")
        console.write("------------------------------")
        getCurrentUserUseCase.invoke().getOrElse {
            console.writeError("Failed To get Current User ${it.message}")
        }
        currentUser?.let {
            console.write("Logged in as: ${it.username} (${it.type})")
        }
    }

    private fun showMainMenu(): Int {
        if (currentUser != null) {
            console.write("\n1. Continue to Dashboard")
            console.write("2. Logout")
        } else {
            console.write("\n1. Login")
            console.write("2. Exit")
        }
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun handleLogin() {
        if (currentUser != null) {
            routeToUserScreen(currentUser!!)
            return
        }

        var attempts = 0
        val maxAttempts = 3

        while (attempts < maxAttempts) {
            console.write("\n=== Login ===")
            val credentials = getCredentials() ?: continue

            loginUseCase(credentials.first, credentials.second).fold(
                onSuccess = { user ->
                    currentUser = user
                    console.write("\nWelcome, ${user.username}!")
                    console.write("Press Enter to continue...")
                    console.read()
                    routeToUserScreen(user)
                    return
                },
                onFailure = { error ->
                    attempts++
                    val remainingAttempts = maxAttempts - attempts
                    console.writeError("Login failed: ${error.message}")
                    if (remainingAttempts > 0) {
                        console.write("Remaining attempts: $remainingAttempts")
                    } else {
                        console.write("\nMaximum login attempts reached. Please try again later.")
                        console.write("Press Enter to continue...")
                        console.read()
                        return
                    }
                }
            )
        }
    }

    private fun getCredentials(): Pair<String, String>? {
        console.write("Username (or 'back' to return): ")
        val username = console.read().trim()

        if (username.equals("back", ignoreCase = true)) {
            return null
        }

        if (username.isBlank()) {
            console.writeError("Username cannot be empty")
            return null
        }

        console.write("Password: ")
        val password = console.read().trim()

        if (password.isBlank()) {
            console.writeError("Password cannot be empty")
            return null
        }

        return username to password
    }

    private fun routeToUserScreen(user: UserEntity) {
        try {
            when (user.type) {
                UserType.ADMIN -> adminScreen.show()
                UserType.MATE -> mateScreen.show()
            }
        } catch (e: Exception) {
            console.writeError("Error in ${user.type} screen: ${e.message}")
            console.write("\nPress Enter to continue...")
            console.read()
        }
    }

    private fun exit() {
        console.write("\nThank you for using PlanMate!")
        console.write("Goodbye!")
        exitProcess(0)
    }

    companion object {
        const val VERSION = "1.0.0"
    }

    private fun logout() {
        logoutUseCase().fold(
            onSuccess = {
                console.write("\nYou have been logged out successfully.")
                currentUser = null
            },
            onFailure = { error ->
                console.writeError("Logout failed: ${error.message}")
            }
        )
    }
}