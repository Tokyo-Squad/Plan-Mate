package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import fakeData.fakeAdminEntity
import fakeData.fakeProjectEntity
import fakeData.fakeRegularUserEntity
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.UpdateProjectUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class UpdateProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = UpdateProjectUseCase(mockRepo)

    private val adminUser = fakeAdminEntity()
    private val regularUser = fakeRegularUserEntity()
    private val testProject = fakeProjectEntity()

    @Test
    fun `should throw UserActionNotAllowedException when user is not admin`() = runTest {
        val exception = assertThrows<PlanMateException.UserActionNotAllowedException> {
            useCase(testProject, regularUser)
        }

        assertThat(exception).hasMessageThat().contains("not authorized")
    }

    @Test
    fun `should throw ValidationException when project name is blank`() = runTest {
        val blankProject = testProject.copy(name = "")

        val exception = assertThrows<PlanMateException.ValidationException> {
            useCase(blankProject, adminUser)
        }

        assertThat(exception).hasMessageThat().contains("cannot be blank")
    }

    @Test
    fun `should return updated project when user is admin and project valid`() = runTest {
        coEvery { mockRepo.updateProject(any(), any()) } returns testProject

        val result = useCase(testProject, adminUser)

        assertThat(result).isEqualTo(testProject)
    }

    @Test
    fun `should call repository with correct project and user ID`() = runTest {
        coEvery { mockRepo.updateProject(any(), any()) } returns testProject

        useCase(testProject, adminUser)

        coVerify(exactly = 1) {
            mockRepo.updateProject(
                match { it.id == testProject.id && it.name == testProject.name },
                adminUser.id
            )
        }
    }

    @Test
    fun `should propagate repository exceptions`() = runTest {
        val expectedError = RuntimeException("Database error")
        coEvery { mockRepo.updateProject(any(), any()) } throws expectedError

        val exception = assertThrows<RuntimeException> {
            useCase(testProject, adminUser)
        }

        assertThat(exception).isSameInstanceAs(expectedError)
    }

    @Test
    fun `should throw ConcurrentModificationException when version mismatch`() = runTest {
        coEvery { mockRepo.updateProject(any(), any()) } throws ConcurrentModificationException()

        assertThrows<ConcurrentModificationException> {
            useCase(testProject, adminUser)
        }
    }

    @Test
    fun `should throw ValidationException when project name is whitespace only`() = runTest {
        val whitespaceProject = testProject.copy(name = "   ")

        val exception = assertThrows<PlanMateException.ValidationException> {
            useCase(whitespaceProject, adminUser)
        }

        assertThat(exception).hasMessageThat().contains("cannot be blank")
    }

}