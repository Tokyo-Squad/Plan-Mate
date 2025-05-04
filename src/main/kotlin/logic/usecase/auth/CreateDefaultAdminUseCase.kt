package logic.usecase.auth

import data.csvfile.UserCsvImpl
import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.entity.UserType
import java.util.*

class CreateDefaultAdminUseCase(private val userRepository: DataProvider<UserEntity>) {
    operator fun invoke() {
        if (userRepository !is UserCsvImpl) {
            throw IllegalArgumentException("Expected UserCsvImpl, got ${userRepository.javaClass.name}")
        }
        try {
            // Check if admin already exists
            val existingAdmin = userRepository.get().find { it.type == UserType.ADMIN }

            if (existingAdmin != null) {
                println("Admin user already exists")
                return
            }

            // Create new admin if none exists
            val adminUser = UserEntity(
                id = UUID.randomUUID(),
                username = "admin",
                password = "admin123", // You should hash this password
                type = UserType.ADMIN,
            )

            userRepository.add(adminUser)
            println("Admin user created successfully")


        } catch (e: Exception) {
            println("Failed to initialize admin user: ${e.message}")
            throw e
        }
    }

}