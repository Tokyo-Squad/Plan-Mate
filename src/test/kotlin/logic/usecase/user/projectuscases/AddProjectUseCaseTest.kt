package logic.usecase.user.projectuscases

import com.google.common.truth.Truth.assertThat
import fakeData.fakeAdminEntity
import fakeData.fakeProjectEntity
import fakeData.fakeRegularUserEntity
import io.mockk.every
import io.mockk.mockk
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.user.AddProjectUseCase
import org.example.utils.PlanMatException
import kotlin.test.Test

class AddProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = AddProjectUseCase(mockRepo)

    private val adminUser = fakeAdminEntity()
    private val regularUser = fakeRegularUserEntity()
    private val testProject = fakeProjectEntity()

    @Test
    fun `should fail when user is not admin`() {
        val result = useCase(testProject, regularUser)

        assertThat(result.exceptionOrNull())
            .isInstanceOf(PlanMatException.UserActionNotAllowedException::class.java)
    }

    @Test
    fun `should fail when project name is blank`() {
        val blankProject = testProject.copy(name = "")
        val result = useCase(blankProject, adminUser)

        assertThat(result.exceptionOrNull())
            .isInstanceOf(PlanMatException.ValidationException::class.java)
    }

    @Test
    fun `should succeed when user is admin and project valid`() {
        every { mockRepo.addProject(any()) } returns Result.success(testProject)

        val result = useCase(testProject, adminUser)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `should return project when creation succeeds`() {
        every { mockRepo.addProject(any()) } returns Result.success(testProject)

        val result = useCase(testProject, adminUser)

        assertThat(result.getOrNull()).isEqualTo(testProject)
    }

    @Test
    fun `should propagate repository failure`() {
        val error = RuntimeException("DB error")
        every { mockRepo.addProject(any()) } returns Result.failure(error)

        val result = useCase(testProject, adminUser)

        assertThat(result.exceptionOrNull()).isEqualTo(error)
    }
}