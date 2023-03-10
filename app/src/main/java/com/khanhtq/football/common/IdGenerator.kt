package com.khanhtq.football.common

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object IdGenerator {
    fun generateMatchId(matchTime: Long, matchDesc: String): String? {
        return try {
            val input: String = matchDesc + matchTime
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hash: ByteArray = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
            val hexString = StringBuilder()
            for (b in hash) {
                hexString.append(String.format("%02x", b))
            }
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }
}