package org.example.di

import data.csvfile.*
import org.example.data.AuthProvider
import org.example.data.DataProvider
import org.example.data.csvfile.AuthProviderImpl
import org.example.data.repository.AuditLogRepositoryImpl
import org.example.data.repository.AuthenticationRepositoryImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.StateRepositoryImpl
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.repository.UserRepositoryImpl
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.example.entity.StateEntity
import org.example.entity.TaskEntity
import org.example.entity.UserEntity
import org.example.logic.repository.AuditLogRepository
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.ProjectRepository
import org.example.logic.repository.StateRepository
import org.example.logic.repository.TaskRepository
import org.example.logic.repository.UserRepository
import org.example.utils.hasher.PasswordMD5HasherImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module{

        single(named("projects")) { "projects.csv" }
        single(named("states")) { "states.csv" }
        single(named("tasks")) { "tasks.csv" }
        single(named("users")) { "users.csv" }
        single(named("auditLogs")) { "audit_logs.csv" }
        single(named("auth")) { "auth.csv" }

        single { PasswordMD5HasherImpl() }
        single<DataProvider<ProjectEntity>> { ProjectCsvImpl(get(named("projects"))) }
        single<DataProvider<StateEntity>> { StateCsvImpl(get(named("states"))) }
        single<DataProvider<TaskEntity>> { TaskCsvImpl(get(named("tasks"))) }
        single<DataProvider<UserEntity>> { UserCsvImpl(get(named("users"))) }
        single<DataProvider<AuditLogEntity>> { AuditLogCsvImpl(get(named("auditLogs"))) }

        single<AuthProvider> { AuthProviderImpl(get(named("auth"))) }

        single<AuditLogRepository> { AuditLogRepositoryImpl(get()) }
        single<AuthenticationRepository> { AuthenticationRepositoryImpl(get(), get(), get(),get()) }
        single<ProjectRepository> { ProjectRepositoryImpl(get(), get()) }
        single<StateRepository> { StateRepositoryImpl(get()) }
        single<TaskRepository> { TaskRepositoryImpl(get(), get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }
