package org.example.di

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.UuidRepresentation
import org.example.data.mongo.MongoDBClient
import org.koin.dsl.module
import java.io.File
import java.util.*

val mongoModule = module {
    single {
        val properties = Properties().apply {
            File("local.properties").inputStream().use { inputStream ->
                load(inputStream)
            }
        }

        val username = properties.getProperty("mongodb.username")
            ?: throw IllegalStateException("MongoDB username not found in local.properties")
        val password = properties.getProperty("mongodb.password")
            ?: throw IllegalStateException("MongoDB password not found in local.properties")
        MongoDBClient(username, password)
    }

    single {
        get<MongoDBClient>().getDatabase()
    }
}