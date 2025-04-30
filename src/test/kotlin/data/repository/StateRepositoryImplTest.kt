package data.repository

import fakeData.StateFakeData
import io.mockk.*
import org.example.data.DataProvider
import org.example.data.repository.StateRepositoryImpl
import org.example.entity.StateEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class StateRepositoryImplTest {
    private lateinit var stateRepositoryImpl: StateRepositoryImpl
    private lateinit var dataProvider: DataProvider<StateEntity>

    @BeforeEach
    fun setUp() {
        dataProvider = mockk(relaxed = true)
        stateRepositoryImpl = StateRepositoryImpl(dataProvider)
    }

    @Test
    fun `should create a new state when state is valid`() {
        // Given
        val state = StateFakeData().createState()

        // When
        val returnedId = stateRepositoryImpl.createState(state)

        // Then
        verify(exactly = 1) { dataProvider.add(state) }
        assertEquals(state.id.toString(), returnedId)
    }

    @Test
    fun `should update state when state exists`() {
        // Given
        val existingState = StateFakeData().createState()
        val updatedState = existingState.copy(name = "New Name")

        every { dataProvider.getById(existingState.id) } returns existingState

        // When
        stateRepositoryImpl.updateState(existingState, updatedState)

        // Then
        verify(exactly = 1) { dataProvider.update(updatedState) }
    }

    @Test
    fun `should throw when updating state that does not exist`() {
        // Given
        val oldState = StateFakeData().createState()
        val newState = oldState.copy(name = "Newer")

        every { dataProvider.getById(oldState.id) } returns null

        // When & Then
        assertThrows(NoSuchElementException::class.java) {
            stateRepositoryImpl.updateState(oldState, newState)
        }
    }

    @Test
    fun `should delete state when state exists`() {
        // Given
        val state = StateFakeData().createState()
        every { dataProvider.getById(state.id) } returns state

        // When
        val result = stateRepositoryImpl.deleteState(state)

        // Then
        verify(exactly = 1) { dataProvider.delete(state.id) }
        assertTrue(result)
    }

    @Test
    fun `should return false when deleting state that does not exist`() {
        // Given
        val state = StateFakeData().createState()
        every { dataProvider.getById(state.id) } returns null

        // When
        val result = stateRepositoryImpl.deleteState(state)

        // Then
        assertFalse(result)
    }
}