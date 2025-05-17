package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.example.data.local.csvfile.UserCsvImpl
import org.example.data.util.exception.FileException
import org.example.entity.User
import org.example.entity.UserType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var userCsv: UserCsvImpl
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        file = File(tempDir, "users.csv")
        userCsv = UserCsvImpl(file.absolutePath)
        user = User(
            id = UUID.randomUUID(), username = "user1", password = "password123", type = UserType.ADMIN
        )
    }

    @Test
    fun shouldReturnEntity_whenAddUser() = runTest {
        userCsv.add(user)
        val all = userCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() = runTest {
        userCsv.add(user)
        val result = userCsv.getById(user.id)
        assertThat(result).isEqualTo(user)
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() = runTest {
        userCsv.add(user)
        val updated = user.copy(username = "updatedUser")
        userCsv.update(updated)
        val result = userCsv.getById(updated.id)
        assertThat(result?.username).isEqualTo("updatedUser")
    }

    @Test
    fun shouldReturnNull_whenEntityNotFound() = runTest {
        val result = userCsv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }

    @Test
    fun shouldReturnNull_whenFileIsEmptyAndIdNotFound() = runTest {
        file.writeText("")
        val result = userCsv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() = runTest {
        val nonExistent = user.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            userCsv.update(nonExistent)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() = runTest {
        userCsv.add(user)
        userCsv.delete(user.id)
        assertThat(userCsv.get()).isEmpty()
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() = runTest {
        file.writeText("")
        val result = userCsv.get()
        assertThat(result).isEmpty()
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() = runTest {
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "users.csv")
        val failingCsv = UserCsvImpl(failingFile.absolutePath)
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.add(user)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() = runTest {
        file.writeText("invalid,line,also-invalid")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            userCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() = runTest {
        file.createNewFile()
        assertThat(file.exists()).isTrue()
        userCsv.add(user)
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() = runTest {
        val readOnlyFile = File(tempDir, "users.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = UserCsvImpl(readOnlyFile.absolutePath)
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.add(user)
        }
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun shouldThrowFileReadException_whenFileContentIsMalformed() = runTest {
        file.writeText("invalid,line,malformed,content")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            userCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line:")
    }

    @Test
    fun shouldSuccessfullyParseFile_whenValidContent() = runTest {
        val validUser = User(
            id = UUID.randomUUID(), username = "ValidUser", password = "ValidPassword123", type = UserType.ADMIN
        )
        file.writeText("${validUser.id},${validUser.username},${validUser.password},${validUser.type}")
        val result = userCsv.get()
        assertThat(result.first()).isEqualTo(validUser)
    }

    @Test
    fun shouldReturnNotEmpty_whenFileContainsEmptyLines() = runTest {
        val validUser = User(
            id = UUID.randomUUID(), username = "ValidUser", password = "ValidPassword123", type = UserType.ADMIN
        )
        file.writeText("\n\n${validUser.id},${validUser.username},${validUser.password},${validUser.type}\n\n")
        val result = userCsv.get()
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validUser)
    }

    @Test
    fun shouldThrowItemNotFoundException_whenUpdatingNonExistentEntity() = runTest {
        val nonExistentUser = user.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            userCsv.update(nonExistentUser)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowException_whenUserTypeIsInvalid() = runTest {
        file.writeText("${UUID.randomUUID()},Test,password123,INVALID_TYPE")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            userCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line:")
    }

    @Test
    fun shouldThrowException_whenUserIdIsInvalidUUID() = runTest {
        file.writeText("not-a-uuid,Test,password123,ADMIN")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            userCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line:")
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenDeleteFails() = runTest {
        userCsv.add(user)
        val readOnlyFile = File(tempDir, "users.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = UserCsvImpl(readOnlyFile.absolutePath)
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.delete(user.id)
        }
        assertThat(exception).hasMessageThat().contains("Error deleting user")
    }

    @Test
    fun shouldThrowFileWriteException_whenUpdateFails() = runTest {
        userCsv.add(user)
        val readOnlyFile = File(tempDir, "users.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = UserCsvImpl(readOnlyFile.absolutePath)
        val updatedUser = user.copy(username = "UpdatedUser")
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.update(updatedUser)
        }
        assertThat(exception).hasMessageThat().contains("Error updating user")
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntityInNonEmptyFile() = runTest {
        userCsv.add(user)
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            userCsv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnNull_whenLookingForNonexistentIdInNonEmptyFile() = runTest {
        userCsv.add(user)
        val result = userCsv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }
}