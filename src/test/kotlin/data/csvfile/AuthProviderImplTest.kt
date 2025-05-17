package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.example.data.local.csvfile.AuthProviderImpl
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.PlanMateException
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
    fun shouldStoreUser_whenAddCurrentUser() = runTest {
        authProvider.addCurrentUser(user)
        val result = authProvider.getCurrentUser()
        assertThat(result).isEqualTo(user)
    }

    @Test
    fun shouldDeleteUser_whenDeleteCurrentUser() = runTest {
        authProvider.addCurrentUser(user)
        authProvider.deleteCurrentUser()

        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            authProvider.getCurrentUser()
        }
        assertThat(exception).hasMessageThat().contains("No current user found.")
    }

    @Test
    fun shouldThrowItemNotFoundException_whenNoUserStored() = runTest {
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            authProvider.getCurrentUser()
        }
        assertThat(exception).hasMessageThat().contains("No current user found.")
    }

    @Test
    fun shouldThrowFileWriteException_whenFileWriteFails() = runTest {
        val readOnlyFile = File(tempDir, "readonly.csv").apply {
            createNewFile()
            setWritable(false)
        }
        val failingProvider = AuthProviderImpl(readOnlyFile.absolutePath)

        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingProvider.addCurrentUser(user)
        }
        assertThat(exception).hasMessageThat().contains("Error writing")
    }

    @Test
    fun shouldThrowFormatException_whenUserFileIsMalformed() = runTest {
        file.writeText("invalid,line,content")

        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            authProvider.getCurrentUser()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line")
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() = runTest {
        val fileWithNoPermission = File(tempDir, "no_permission.csv").apply {
            createNewFile()
            setReadable(true)
            setWritable(false)
        }
        val failingProvider = AuthProviderImpl(fileWithNoPermission.absolutePath)

        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingProvider.addCurrentUser(user)
        }
        assertThat(exception).hasMessageThat().contains("Error writing current user to file")
    }

    @Test
    fun shouldThrowFileWriteException_whenErrorDeletingUser() = runTest {
        val fileWithNoPermission = File(tempDir, "no_permission_delete.csv").apply {
            createNewFile()
            setWritable(true)
        }
        val failingProvider = AuthProviderImpl(fileWithNoPermission.absolutePath)
        failingProvider.addCurrentUser(user)
        fileWithNoPermission.setWritable(false)

        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingProvider.deleteCurrentUser()
        }
        assertThat(exception).hasMessageThat().contains("Error deleting current user")
    }

    @Test
    fun shouldThrowItemNotFoundException_whenFileIsEmpty() = runTest {
        val emptyFile = File(tempDir, "empty.csv").apply { createNewFile() }
        val provider = AuthProviderImpl(emptyFile.absolutePath)

        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            provider.getCurrentUser()
        }
        assertThat(exception).hasMessageThat().contains("No current user found.")
    }

    @Test
    fun shouldThrowFileWriteException_whenAddUserFails() = runTest {
        val readOnlyFile = File(tempDir, "current_user.csv").apply {
            createNewFile()
            setReadable(true)
            setWritable(false)
        }

        val failingProvider = AuthProviderImpl(readOnlyFile.absolutePath)
        val newUser = user.copy(username = "UpdatedUser")

        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingProvider.addCurrentUser(newUser)
        }
        assertThat(exception).hasMessageThat().contains("Error writing current user to file")
    }
}