package org.example.di

import data.csvfile.*
import org.example.data.csvfile.AuthProviderImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named("projects")) { "projects.csv" }
    single(named("states")) { "states.csv" }
    single(named("tasks")) { "tasks.csv" }
    single(named("users")) { "users.csv" }
    single(named("auditLogs")) { "audit_logs.csv" }
    single(named("auth")) { "auth.csv" }

    single { ProjectCsvImpl((get(named("projects")))) }
    single { AuditLogCsvImpl(get(named("auditLogs"))) }
    single { TaskCsvImpl(get(named("tasks"))) }
    single { UserCsvImpl(get(named("users"))) }
    single { StateCsvImpl(get(named("states"))) }
    single { AuthProviderImpl(get(named("auth"))) }
    single { ProjectRepositoryImpl(get(), get()) }
}