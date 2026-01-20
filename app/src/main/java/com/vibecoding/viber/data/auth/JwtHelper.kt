package com.vibecoding.viber.data.auth

import io.jsonwebtoken.Jwts
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.StringReader
import java.security.PrivateKey
import java.util.Date

/**
 * Helper class for generating JWTs for GitHub App authentication.
 */
object JwtHelper {
    
    /**
     * Generate a JWT for authenticating as a GitHub App.
     * 
     * @param appId The GitHub App ID
     * @param privateKeyPem The private key in PEM format
     * @return The signed JWT string
     */
    fun generateAppJwt(appId: String, privateKeyPem: String): String {
        val privateKey = parsePrivateKey(privateKeyPem)
        
        val now = Date()
        // JWT expires in 10 minutes (max allowed by GitHub)
        val expiration = Date(now.time + 10 * 60 * 1000)
        // Issue time is 60 seconds in the past to account for clock drift
        val issuedAt = Date(now.time - 60 * 1000)
        
        return Jwts.builder()
            .issuer(appId)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(privateKey, Jwts.SIG.RS256)
            .compact()
    }
    
    /**
     * Parse a PEM-encoded private key.
     */
    private fun parsePrivateKey(privateKeyPem: String): PrivateKey {
        // Handle escaped newlines from BuildConfig
        val normalizedPem = privateKeyPem.replace("\\n", "\n")
        
        val pemParser = PEMParser(StringReader(normalizedPem))
        val pemObject = pemParser.readObject()
        pemParser.close()
        
        val converter = JcaPEMKeyConverter()
        
        return when (pemObject) {
            is PrivateKeyInfo -> converter.getPrivateKey(pemObject)
            is org.bouncycastle.openssl.PEMKeyPair -> converter.getPrivateKey(pemObject.privateKeyInfo)
            else -> throw IllegalArgumentException("Unsupported key format: ${pemObject?.javaClass?.name}")
        }
    }
}
