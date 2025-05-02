package data.csvfile

import com.google.common.truth.Truth.assertThat
import org.example.data.csvfile.AuthProviderImpl
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AuthProviderImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var authProvider: AuthProviderImpl
    private lateinit var user: UserEntity

    @BeforeEach
    fun setup() {
        file = File(tempDir, "current_user.csv")
        authProvider = AuthProviderImpl(file.absolutePath)
        user = UserEntity(
            id = UUID.randomUUID(),
            username = "user1",
            password = "password123",
            type = UserType.ADMIN
        )
    }

    @Test
    fun shouldStoreUser_whenAddCurrentUser() {
        // Given
        val userToAdd = user

        // When
        authProvider.addCurrentUser(userToAdd)

        // Then
        val result = authProvider.getCurrentUser()
        assertThat(result).isEqualTo(userToAdd)
    }

    @Test
    fun shouldDeleteUser_whenDeleteCurrentUser() {
        // Given
        authProvider.addCurrentUser(user)

        // When
        authProvider.deleteCurrentUser()

        // Then
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            authProvider.getCurrentUser()
        }
        assertThat(exception).hasMessageThat().contains("No current user found.")
    }

    @Test
    fun shouldThrowItemNotFoundException_whenNoUserStored() {
        // Given
        // No user is added

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            authProvider.getCurrentUser()
        }

        // Then
        assertThat(exception).hasMessageThat().contains("No current user found.")
    }

    @Test
    fun shouldThrowFileWriteException_whenFileWriteFails() {
        // Given
        val readOnlyFile = File(tempDir, "readonly.csv").apply {
            createNewFile()
            setWritable(false)
        }
        val failingProvider = AuthProviderImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingProvider.addCurrentUser(user)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing")
    }

    @Test
    fun shouldThrowFormatException_whenUserFileIsMalformed() {
        // Given
        file.writeText("invalid,line,content")

        // When
        val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
            authProvider.getCurrentUser()
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Malformed CSV line")
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() {
        // Given
        val fileWithNoPermission = File(tempDir, "no_permission.csv").apply {
            createNewFile()
            setReadable(true)
            setWritable(false)
        }
        val failingProvider = AuthProviderImpl(fileWithNoPermission.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingProvider.addCurrentUser(user)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing current user to file")
    }

    @Test
    fun shouldThrowFileWriteException_whenErrorDeletingUser() {
        // Given
        val fileWithNoPermission = File(tempDir, "no_permission_delete.csv").apply {
            createNewFile()
            setWritable(true)
        }
        val failingProvider = AuthProviderImpl(fileWithNoPermission.absolutePath)
        failingProvider.addCurrentUser(user)
        fileWithNoPermission.setWritable(false)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingProvider.deleteCurrentUser() // Attempt to delete user with write permission issue
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error deleting current user")
    }

    @Test
    fun shouldThrowItemNotFoundException_whenFileIsEmpty() {
        // Given
        val emptyFile = File(tempDir, "empty.csv").apply { createNewFile() }
        val provider = AuthProviderImpl(emptyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            provider.getCurrentUser()
        }

        // Then
        assertThat(exception).hasMessageThat().contains("No current user found.")
    }

    @Test
    fun shouldThrowFileWriteException_whenAddUserFails() {
        // GIVEN
        val readOnlyFile = File(tempDir, "current_user.csv").apply {
            createNewFile()
            setReadable(true)
            setWritable(false)
        }

        val failingProvider = AuthProviderImpl(readOnlyFile.absolutePath)
        val newUser = user.copy(username = "UpdatedUser")

        // WHEN
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingProvider.addCurrentUser(newUser)
        }

        // THEN
        assertThat(exception).hasMessageThat().contains("Error writing current user to file")
    }


}