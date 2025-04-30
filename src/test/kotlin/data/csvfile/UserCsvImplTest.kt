package data.csvfile

import com.google.common.truth.Truth.assertThat
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var userCsv: UserCsvImpl
    private lateinit var user: UserEntity

    @BeforeEach
    fun setup() {
        file = File(tempDir, "users.csv")
        userCsv = UserCsvImpl(file.absolutePath)
        user = UserEntity(
            id = UUID.randomUUID(),
            userName = "user1",
            password = "password123",
            type = UserType.ADMIN
        )
    }

    @Test
    fun shouldReturnEntity_whenAddUser() {
        // When
        userCsv.add(user)

        // Then
        val all = userCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() {
        // Given
        userCsv.add(user)

        // When
        val result = userCsv.getById(user.id)

        // Then
        assertThat(result).isEqualTo(user)
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() {
        // Given
        userCsv.add(user)
        val updated = user.copy(userName = "updatedUser")

        // When
        userCsv.update(updated)

        // Then
        val result = userCsv.getById(updated.id)
        assertThat(result?.userName).isEqualTo("updatedUser")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistent = user.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            userCsv.update(nonExistent)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() {
        // Given
        userCsv.add(user)

        // When
        userCsv.delete(user.id)

        // Then
        assertThat(userCsv.get()).isEmpty()
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntity() {
        // When / Then
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            userCsv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() {
        // Given
        file.writeText("")

        // When
        val result = userCsv.get()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() {
        // Given
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "users.csv")

        // When
        val failingCsv = UserCsvImpl(failingFile.absolutePath)

        // Then
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(user)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() {
        // Given
        file.writeText("invalid,line,also-invalid")

        // Then
        val exception = assertFailsWith<IllegalArgumentException> {
            userCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() {
        // Given
        file.createNewFile()
        assertThat(file.exists()).isTrue()

        val userCsv = UserCsvImpl(file.absolutePath)

        // When
        userCsv.add(user)

        // Then
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() {
        // Given
        val readOnlyFile = File(tempDir, "users.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = UserCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(user)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }
}
