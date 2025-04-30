package org.example.di

import org.example.data.AuditLogCsvImpl
import org.example.data.DataProvider
import org.example.data.ProjectCsvImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import org.koin.dsl.module


val appModule = module {
    single<DataProvider<ProjectEntity>> {
        ProjectCsvImpl("projects.csv")
    }
    single<DataProvider<AuditLogEntity>> {
        AuditLogCsvImpl("audit_logs.csv")
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(get())
    }
}