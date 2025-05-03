package org.example.utils.hasher

import org.example.utils.PlanMateException
import java.security.MessageDigest

class PasswordMD5HasherImpl : PasswordHasher {
    override fun hash(password: String): String {
        require(password.isNotBlank()) { "Password cannot be blank" }

        return try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(password.toByteArray())
            val messageDigest = digest.digest()

            val hexString = StringBuilder()
            for (byte in messageDigest) {
                hexString.append(String.format("%02x", byte))
            }
            hexString.toString()
        } catch (e: Exception) {
            throw PlanMateException.HashingException("Failed to hash password")
        }
    }
}