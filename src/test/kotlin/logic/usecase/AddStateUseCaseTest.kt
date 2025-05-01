package logic.usecase

import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.AddStateUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AddStateUseCaseTest {
    private lateinit var stateRepository: StateRepository
    private lateinit var addStateUseCase: AddStateUseCase
    private val fakeData = StateFakeData()

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)

        addStateUseCase = AddStateUseCase(stateRepository)
    }

    @Test
    fun `invoke returns generated id on success`() {
        // Given
        val state = fakeData.createState()
        val expectedId = state.id.toString()
        every { stateRepository.addState(state) } returns expectedId

        // When
        val result = addStateUseCase(state)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())
        verify(exactly = 1) { stateRepository.addState(state) }
    }

    @Test
    fun `invoke returns failure when repository throws`() {
        // Given
        val state = fakeData.createState()
        val ex = RuntimeException("error")
        every { stateRepository.addState(state) } throws ex

        // When
        val result = addStateUseCase(state)

        // Then
        assertTrue(result.isFailure)
        assertEquals(ex, result.exceptionOrNull())
    }

}