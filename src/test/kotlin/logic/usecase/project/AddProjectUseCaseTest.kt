package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import fakeData.fakeAdminEntity
import fakeData.fakeProjectEntity
import fakeData.fakeRegularUserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.AddProjectUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class AddProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = AddProjectUseCase(mockRepo)

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
    fun `should return project when user is admin and project valid`() = runTest {
        coEvery { mockRepo.addProject(any()) } returns testProject

        val result = useCase(testProject, adminUser)

        assertThat(result).isEqualTo(testProject)
    }

    @Test
    fun `should propagate repository exceptions`() = runTest {
        val error = RuntimeException("DB error")
        coEvery { mockRepo.addProject(any()) } throws error

        val exception = assertThrows<RuntimeException> {
            useCase(testProject, adminUser)
        }

        assertThat(exception).isEqualTo(error)
    }

    @Test
    fun `should call repository exactly once`() = runTest {
        coEvery { mockRepo.addProject(any()) } returns testProject

        useCase(testProject, adminUser)

        coVerify(exactly = 1) { mockRepo.addProject(any()) }
    }
}