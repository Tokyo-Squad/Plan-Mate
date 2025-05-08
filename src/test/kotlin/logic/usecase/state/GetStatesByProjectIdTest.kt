package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.GetStatesByProjectId
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test

class GetStatesByProjectIdTest {

    private lateinit var repo: StateRepository
    private lateinit var useCase: GetStatesByProjectId
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk()
        useCase = GetStatesByProjectId(repo)
    }

    @Test
    fun `should return list of states when repository returns valid data`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        val expectedStates = listOf(
            fake.createState(projectId = projectId),
            fake.createState(projectId = projectId)
        )
        coEvery { repo.getByProjectId(projectId) } returns expectedStates

        // When
        val result = useCase(projectId)

        // Then
        assertThat(result).isEqualTo(expectedStates)
    }

    @Test
    fun `should throw exception when repository throws error`() = runTest {
        // Given
        val projectId = UUID.randomUUID()
        val ex = PlanMateException.ItemNotFoundException("No states found")
        coEvery { repo.getByProjectId(projectId) } throws ex

        // When / Then
        val thrown = runCatching { useCase(projectId) }.exceptionOrNull()
        assertThat(thrown).isSameInstanceAs(ex)
    }
}