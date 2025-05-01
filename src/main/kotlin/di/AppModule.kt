package org.example.di

import data.csvfile.*
import org.koin.core.qualifier.named
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