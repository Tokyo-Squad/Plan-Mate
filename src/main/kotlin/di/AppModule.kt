package org.example.di

import data.csvfile.*
import org.koin.core.qualifier.named
import org.example.data.AuditLogCsvImpl
import org.example.data.DataProvider
import org.example.data.ProjectCsvImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import org.koin.dsl.module

val appModule = module {
    single(named("projects")) { "projects.csv" }
    single(named("states")) { "states.csv" }
    single(named("tasks")) { "tasks.csv" }
    single(named("users")) { "users.csv" }
    single(named("auditLogs")) { "audit_logs.csv" }

    single { ProjectCsvImpl(get(named("projects"))) }
    single { StateCsvImpl(get(named("states"))) }
    single { TaskCsvImpl(get(named("tasks"))) }
    single { UserCsvImpl(get(named("users"))) }
    single { AuditLogCsvImpl(get(named("auditLogs"))) }
}

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