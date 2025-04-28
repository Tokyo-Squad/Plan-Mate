package data.repository

import fakeData.StateFakeData
import io.mockk.mockk
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
    fun `should create a new state when given valid state entity`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should update an existing state when given valid state ID and new state data`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should delete a state when given a valid state ID`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should return false when trying to delete a non-existent state`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should create state CSV file when it does not exist`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should not create new state CSV file when it already exists`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should throw error when trying to update a non-existent state`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should throw error when trying to create a state with missing mandatory fields`() {
        TODO("Implementation of test case")
    }

}