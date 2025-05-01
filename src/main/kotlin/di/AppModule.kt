package org.example.di

import data.csvfile.AuditLogCsvImpl
import data.csvfile.ProjectCsvImpl
import data.csvfile.TaskCsvImpl
import data.csvfile.UserCsvImpl
import org.example.data.DataProvider
import org.example.data.repository.ProjectRepositoryImpl
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.koin.core.qualifier.named
import org.example.data.repository.*
import org.example.logic.repository.AuditLogRepository
import org.koin.dsl.module

val appModule = module {
    single(named("projects")) { "projects.csv" }
    single(named("states")) { "states.csv" }
    single(named("tasks")) { "tasks.csv" }
    single(named("users")) { "users.csv" }
    single(named("auditLogs")) { "audit_logs.csv" }

    single<DataProvider<ProjectEntity>> { ProjectCsvImpl((get(named("projects")))) }
    single<DataProvider<AuditLogEntity>> { AuditLogCsvImpl(get(named("auditLogs"))) }
    single { TaskCsvImpl(get(named("tasks"))) }
    single { UserCsvImpl(get(named("users"))) }
    single { ProjectRepositoryImpl(get(), get()) }
    single { AuditLogCsvImpl(get(named("auditLogs"))) }
}


}
