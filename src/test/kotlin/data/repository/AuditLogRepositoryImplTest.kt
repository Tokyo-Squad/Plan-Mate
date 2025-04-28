package data.repository

import io.mockk.mockk
import org.example.data.DataProvider
import org.example.data.repository.AuditLogRepositoryImpl
import org.example.entity.AuditLogEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach


class AuditLogRepositoryImplTest {
    private lateinit var auditLogRepository: AuditLogRepositoryImpl
    private var dataProvider: DataProvider<AuditLogEntity> = mockk()

    @BeforeEach
    fun setUp() {
        auditLogRepository = AuditLogRepositoryImpl(dataProvider)
    }

}