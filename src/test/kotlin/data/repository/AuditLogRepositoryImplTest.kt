package data.repository

import io.mockk.mockk
import org.example.data.DataProvider
import org.example.data.repository.AuditLogRepositoryImpl
import org.example.entity.AuditLogEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class AuditLogRepositoryImplTest {
    private lateinit var auditLogRepository: AuditLogRepositoryImpl
    private var dataProvider: DataProvider<AuditLogEntity> = mockk()

    @BeforeEach
    fun setUp() {
        auditLogRepository = AuditLogRepositoryImpl(dataProvider)
    }

    @Test
    fun `should create a new audit log when creating a new project or task`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should save to audit log when making changes to a project or task`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should retrieve audit logs filtered by project ID`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should retrieve audit logs filtered by task ID`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should not allow saving a log with missing mandatory fields`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should throw exception when audit log is empty`() {
        TODO("Implementation of test case")
    }

}