package org.example.di

import data.csvfile.*
import org.example.data.AuthProvider
import org.example.data.DataProvider
import org.example.data.csvfile.AuthProviderImpl
import org.example.data.mongo.*
import org.example.data.repository.*
import org.example.entity.*
import org.example.logic.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named("projects")) { "projects.csv" }
    single(named("states")) { "states.csv" }
    single(named("tasks")) { "tasks.csv" }
    single(named("users")) { "users.csv" }
    single(named("auditLogs")) { "audit_logs.csv" }
    single(named("auth")) { "auth.csv" }

    single<DataProvider<ProjectEntity>>(named("projectDataProvider")) { ProjectCsvImpl(get(named("projects"))) }
    single<DataProvider<ProjectEntity>>(named("projectDataProviderMongo")) { ProjectMongoDBImpl(get()) }
    single<DataProvider<StateEntity>>(named("stateDataProvider")) { StateCsvImpl(get(named("states"))) }
    single<DataProvider<StateEntity>>(named("stateDataProviderMongo")) { StateMongoDBImpl(get()) }
    single<DataProvider<TaskEntity>>(named("taskDataProvider")) { TaskCsvImpl(get(named("tasks"))) }
    single<DataProvider<TaskEntity>>(named("taskDataProviderMongo")) { TaskMongoDBImpl(get()) }
    single<DataProvider<UserEntity>>(named("userDataProvider")) { UserCsvImpl(get(named("users"))) }
    single<DataProvider<UserEntity>>(named("userDataProviderMongo")) { UsersMongoImpl(get()) }
    single<DataProvider<AuditLogEntity>>(named("auditDataProvider")) { AuditLogCsvImpl(get(named("auditLogs"))) }
    single<DataProvider<AuditLogEntity>>(named("auditDataProviderMongo")) { AuditLogMongoDbImpl(get()) }
    single<AuthProvider> { AuthProviderImpl(get(named("auth"))) }
    single<AuthProvider>(named("authProviderMongo")) { AuthMongoImpl(get()) }



    single<AuditLogRepository> { AuditLogRepositoryImpl(get(qualifier = named("auditDataProviderMongo"))) }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            get(named("authProviderMongo")),
            get(),
            get(qualifier = named("userDataProviderMongo"))
        )
    }
    single<ProjectRepository> {
        ProjectRepositoryImpl(
            get(
                qualifier = named("projectDataProviderMongo")
            ), get(
                qualifier = named("auditDataProviderMongo")
            )
        )
    }
    single<StateRepository> {
        StateRepositoryImpl(
            get(
                qualifier = named("stateDataProviderMongo")
            )
        )
    }
    single<TaskRepository> {
        TaskRepositoryImpl(
            get(), get(
                qualifier = named("taskDataProviderMongo")
            ), get()
        )
    }
    single<UserRepository> {
        UserRepositoryImpl(
            get(
                qualifier = named("userDataProviderMongo")
            )
        )
    }
}