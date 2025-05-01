package logic.usecase

import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.GetStateByIdUseCase
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class GetStateUseCaseTest {
    private lateinit var stateRepository: StateRepository
    private lateinit var getStateByIdUseCase: GetStateByIdUseCase
    private val fakeData = StateFakeData()

    @BeforeTest fun setUp() {
        stateRepository = mockk()
        getStateByIdUseCase = GetStateByIdUseCase(stateRepository)
    }

    @Test fun `invoke returns state when found`() {
        // Given
        val id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val expected = fakeData.createState(id = id, projectId = projectId)
        every { stateRepository.getStateById(id) } returns expected

        // When
        val result = getStateByIdUseCase(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test fun `invoke returns failure when not found`() {
        // Given
        val id = UUID.randomUUID()
        every { stateRepository.getStateById(id) } returns null

        // When
        val result = getStateByIdUseCase(id)

        // Then
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()!!
        assertTrue(ex is NoSuchElementException)
        assertEquals("State with ID $id not found", ex.message)
    }
}
