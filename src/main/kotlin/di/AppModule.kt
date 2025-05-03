package org.example.di

import data.csvfile.*
import org.example.data.csvfile.AuthProviderImpl
import org.example.data.repository.AuditLogRepositoryImpl
import org.example.data.repository.AuthenticationRepositoryImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.StateRepositoryImpl
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.repository.UserRepositoryImpl
import org.example.logic.repository.AuditLogRepository
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.ProjectRepository
import org.example.logic.repository.StateRepository
import org.example.logic.repository.TaskRepository
import org.example.logic.repository.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
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

    single<AuditLogRepository> { AuditLogRepositoryImpl(get()) }
    single<AuthenticationRepository> { AuthenticationRepositoryImpl(get(), get(), get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get(), get()) }
    single<StateRepository> { StateRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}