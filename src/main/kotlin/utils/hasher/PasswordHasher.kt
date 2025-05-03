package org.example.utils.hasher

interface PasswordHasher {
    fun hash(password: String):String
}