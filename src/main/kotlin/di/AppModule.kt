package org.example.di

import data.csvfile.*
import org.example.data.AuthProvider
import org.example.data.DataProvider
import org.example.data.csvfile.AuthProviderImpl
import org.example.data.repository.*
import org.example.entity.*
import org.example.logic.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // CSV filenames
    single(named("projects")) { "projects.csv" }
    single(named("states")) { "states.csv" }
    single(named("tasks")) { "tasks.csv" }
    single(named("users")) { "users.csv" }
    single(named("auditLogs")) { "audit_logs.csv" }
    single(named("auth")) { "auth.csv" }
    // Generic data providers
    single<DataProvider<ProjectEntity>>(named("projectDataProvider")) { ProjectCsvImpl(get(named("projects"))) }
    single<DataProvider<StateEntity>>(named("stateDataProvider")) { StateCsvImpl(get(named("states"))) }
    single<DataProvider<TaskEntity>>(named("taskDataProvider")) { TaskCsvImpl(get(named("tasks"))) }
    single<DataProvider<UserEntity>>(named("userDataProvider")) { UserCsvImpl(get(named("users"))) }
    single<DataProvider<AuditLogEntity>>(named("auditDataProvider")) { AuditLogCsvImpl(get(named("auditLogs"))) }
    // Auth provider
    single<AuthProvider> { AuthProviderImpl(get(named("auth"))) }
    // Repositories
    single<AuditLogRepository> { AuditLogRepositoryImpl(get(qualifier = named("auditDataProvider"))) }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            get(),
            get(),
            get(qualifier = named("userDataProvider"))
        )
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(
            get(
                qualifier = named("projectDataProvider")
            ), get(
                qualifier = named("auditDataProvider")
            )
        )
    }
    single<StateRepository> {
        StateRepositoryImpl(
            get(
                qualifier = named("stateDataProvider")
            )
        )
    }
    single<TaskRepository> {
        TaskRepositoryImpl(
            get(), get(
                qualifier = named("taskDataProvider")
            ), get()
        )
    }
    single<UserRepository> {
        UserRepositoryImpl(
            get(
                qualifier = named("userDataProvider")
            )
        )
    }
}