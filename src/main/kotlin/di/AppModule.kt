package org.example.di

import data.csvfile.*
import org.koin.dsl.module

val appModule = module {
    single { "projects.csv" }
    single { "states.csv" }
    single { "tasks.csv" }
    single { "users.csv" }
    single { "audit_logs.csv" }

    single { ProjectCsvImpl(get()) }
    single { StateCsvImpl(get()) }
    single { TaskCsvImpl(get()) }
    single { UserCsvImpl(get()) }
    single { AuditLogCsvImpl(get()) }
}