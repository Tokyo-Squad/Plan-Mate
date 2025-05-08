package org.example.presentation

import AdminScreen
import MateScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.auth.LoginUseCase
import org.example.logic.usecase.auth.LogoutUseCase
import org.example.presentation.io.ConsoleIO
import org.example.utils.PlanMateException
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

    suspend fun start() {
        // Initialize current user asynchronously
        try {
            currentUser = getCurrentUserUseCase()
        } catch (e: PlanMateException) {
            console.writeError("Failed to get current user: ${e.message}")
        }

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

    private suspend fun showWelcome() {
        withContext(Dispatchers.IO) {
            console.write("\n=== Welcome to PlanMate v${VERSION} ===")
            console.write("A Simple Task Management System")
            console.write("------------------------------")

            // Refresh current user status
            try {
                currentUser = getCurrentUserUseCase()
            } catch (e: PlanMateException) {
                console.writeError("Failed to get Current User: ${e.message}")
            }

            currentUser?.let {
                console.write("Logged in as: ${it.username} (${it.type})")
            }
        }
    }

    private suspend fun showMainMenu(): Int {
        return withContext(Dispatchers.IO) {
            if (currentUser != null) {
                console.write("\n1. Continue to Dashboard")
                console.write("2. Logout")
            } else {
                console.write("\n1. Login")
                console.write("2. Exit")
            }
            console.write("\nSelect an option: ")
            console.read().toIntOrNull() ?: 0
        }
    }

    private suspend fun handleLogin() {
        if (currentUser != null) {
            routeToUserScreen(currentUser!!)
            return
        }

        var attempts = 0
        val maxAttempts = 3

        while (attempts < maxAttempts) {
            console.write("\n=== Login ===")
            val credentials = getCredentials() ?: continue

            try {
                // Call login use case (doesn't return a user)
                loginUseCase(credentials.first, credentials.second)

                // After successful login, fetch the current user
                try {
                    currentUser = getCurrentUserUseCase()
                    if (currentUser != null) {
                        console.write("\nWelcome, ${currentUser!!.username}!")
                        console.write("Press Enter to continue...")
                        console.read()
                        routeToUserScreen(currentUser!!)
                        return
                    } else {
                        console.writeError("Login succeeded but couldn't retrieve user information.")
                    }
                } catch (e: PlanMateException) {
                    console.writeError("Login succeeded but couldn't retrieve user information: ${e.message}")
                }
            } catch (e: PlanMateException) {
                attempts++
                val remainingAttempts = maxAttempts - attempts
                console.writeError("Login failed: ${e.message}")
                if (remainingAttempts > 0) {
                    console.write("Remaining attempts: $remainingAttempts")
                } else {
                    console.write("\nMaximum login attempts reached. Please try again later.")
                    console.write("Press Enter to continue...")
                    console.read()
                    return
                }
            }
        }
    }

    private suspend fun getCredentials(): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            console.write("Username (or 'back' to return): ")
            val username = console.read().trim()

            if (username.equals("back", ignoreCase = true)) {
                return@withContext null
            }

            if (username.isBlank()) {
                console.writeError("Username cannot be empty")
                return@withContext null
            }

            console.write("Password: ")
            val password = console.read().trim()

            if (password.isBlank()) {
                console.writeError("Password cannot be empty")
                return@withContext null
            }

            username to password
        }
    }

    private suspend fun routeToUserScreen(user: UserEntity) {
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

    private suspend fun exit() {
        withContext(Dispatchers.IO) {
            console.write("\nThank you for using PlanMate!")
            console.write("Goodbye!")
            exitProcess(0)
        }
    }

    private suspend fun logout() {
        try {
            logoutUseCase()
            console.write("\nYou have been logged out successfully.")
            currentUser = null
        } catch (e: PlanMateException) {
            console.writeError("Logout failed: ${e.message}")
        }
    }

    companion object {
        const val VERSION = "1.0.0"
    }
}