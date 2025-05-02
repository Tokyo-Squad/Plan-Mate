package logic.usecase.user.projectuscases

import com.google.common.truth.Truth.assertThat
import fakeData.fakeAdminEntity
import fakeData.fakeProjectEntity
import fakeData.fakeRegularUserEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.user.UpdateProjectUseCase
import org.example.utils.PlanMatException
import kotlin.test.Test

class UpdateProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = UpdateProjectUseCase(mockRepo)

    private val adminUser = fakeAdminEntity()
    private val regularUser = fakeRegularUserEntity()
    private val testProject = fakeProjectEntity()

    @Test
    fun `should fail with UserActionNotAllowedException when user is not admin`() {
        val result = useCase(testProject, regularUser)

        assertThat(result.exceptionOrNull())
            .isInstanceOf(PlanMatException.UserActionNotAllowedException::class.java)
    }

    @Test
    fun `should fail with ValidationException when project name is blank`() {
        val blankProject = testProject.copy(name = "")
        val result = useCase(blankProject, adminUser)

        assertThat(result.exceptionOrNull())
            .isInstanceOf(PlanMatException.ValidationException::class.java)
    }

    @Test
    fun `should succeed when user is admin and project valid`() {
        every { mockRepo.updateProject(any(), any()) } returns Result.success(testProject)

        val result = useCase(testProject, adminUser)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `should return updated project when update succeeds`() {
        every { mockRepo.updateProject(any(), any()) } returns Result.success(testProject)

        val result = useCase(testProject, adminUser)

        assertThat(result.getOrNull()).isEqualTo(testProject)
    }

    @Test
    fun `should call repository with correct project and user ID`() {
        every { mockRepo.updateProject(any(), any()) } returns Result.success(testProject)

        useCase(testProject, adminUser)

        verify {
            mockRepo.updateProject(
                match { it.id == testProject.id && it.name == testProject.name },
                match { it == adminUser.id }
            )
        }
    }

    @Test
    fun `should propagate repository failure`() {
        val expectedError = RuntimeException("Database error")
        every { mockRepo.updateProject(any(), any()) } returns Result.failure(expectedError)

        val result = useCase(testProject, adminUser)

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should fail with ValidationException when project name is whitespace only`() {
        val whitespaceProject = testProject.copy(name = "   ")
        val result = useCase(whitespaceProject, adminUser)

        assertThat(result.exceptionOrNull())
            .isInstanceOf(PlanMatException.ValidationException::class.java)
    }

    @Test
    fun `should include validation message when name is blank`() {
        val blankProject = testProject.copy(name = "")
        val result = useCase(blankProject, adminUser)

        assertThat(result.exceptionOrNull())
            .hasMessageThat()
            .contains("Validation failed")
    }
}