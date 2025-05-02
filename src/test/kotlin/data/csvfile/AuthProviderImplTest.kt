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
  authProvider.addCurrentUser(user)

  val result = authProvider.getCurrentUser()
  assertThat(result).isEqualTo(user)
 }

 @Test
 fun shouldDeleteUser_whenDeleteCurrentUser() {
  authProvider.addCurrentUser(user)

  authProvider.deleteCurrentUser()

  val result = authProvider.getCurrentUser()
  assertThat(result).isNull()
 }

 @Test
 fun shouldReturnNull_whenNoUserStored() {
  val result = authProvider.getCurrentUser()
  assertThat(result).isNull()
 }

 @Test
 fun shouldThrowFileWriteException_whenFileWriteFails() {
  val readOnlyFile = File(tempDir, "readonly.csv").apply {
   createNewFile()
   setWritable(false)
  }
  val failingProvider = AuthProviderImpl(readOnlyFile.absolutePath)

  val exception = assertFailsWith<PlanMatException.FileWriteException> {
   failingProvider.addCurrentUser(user)
  }

  assertThat(exception).hasMessageThat().contains("Error writing")
 }

 @Test
 fun shouldThrowFormatException_whenUserFileIsMalformed() {
  file.writeText("invalid,line,content")

  val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
   authProvider.getCurrentUser()
  }

  assertThat(exception).hasMessageThat().contains("Malformed CSV line")
 }
}