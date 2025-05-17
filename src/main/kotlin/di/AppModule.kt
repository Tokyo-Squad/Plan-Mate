package org.example.di

import org.example.data.Authentication
import org.example.data.LocalDataSource
import org.example.data.RemoteDataSource
import org.example.data.local.csvfile.*
import org.example.data.remote.dto.*
import org.example.data.remote.mongo.*
import org.example.data.repository.*
import org.example.entity.*
import org.example.logic.repository.*
import org.example.utils.hasher.PasswordHasher
import org.example.utils.hasher.PasswordMD5HasherImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single <PasswordHasher> { PasswordMD5HasherImpl() }

    single(named("projects")) { "projects.csv" }
    single(named("states")) { "states.csv" }
    single(named("tasks")) { "tasks.csv" }
    single(named("users")) { "users.csv" }
    single(named("auditLogs")) { "audit_logs.csv" }
    single(named("auth")) { "auth.csv" }

    single<LocalDataSource<ProjectEntity>>(named("projectDataProvider")) { ProjectCsvImpl(get(named("projects"))) }
    single<RemoteDataSource<ProjectDto>>(named("projectDataProviderMongo")) { ProjectMongoDBImpl(get()) }
    single<LocalDataSource<StateEntity>>(named("stateDataProvider")) { StateCsvImpl(get(named("states"))) }
    single<RemoteDataSource<StateDto>>(named("stateDataProviderMongo")) { StateMongoDBImpl(get()) }
    single<LocalDataSource<TaskEntity>>(named("taskDataProvider")) { TaskCsvImpl(get(named("tasks"))) }
    single<RemoteDataSource<TaskDto>>(named("taskDataProviderMongo")) { TaskMongoDBImpl(get()) }
    single<LocalDataSource<UserEntity>>(named("userDataProvider")) { UserCsvImpl(get(named("users"))) }
    single<RemoteDataSource<UserDto>>(named("userDataProviderMongo")) { UsersMongoImpl(get()) }
    single<LocalDataSource<AuditLogEntity>>(named("auditDataProvider")) { AuditLogCsvImpl(get(named("auditLogs"))) }
    single<RemoteDataSource<AuditLogDto>>(named("auditDataProviderMongo")) { AuditLogMongoDbImpl(get()) }
    single<Authentication> { AuthCsvImpl(get(named("auth"))) }
    single<Authentication>(named("authProviderMongo")) { AuthMongoImpl(get()) }



    single<AuditLogRepository> { AuditLogRepositoryImpl(get(qualifier = named("auditDataProviderMongo"))) }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            get(named("authProviderMongo")),
            get(),
            get(qualifier = named("userDataProviderMongo")),
            get()
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