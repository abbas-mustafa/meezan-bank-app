package com.example.forgetpassword.mbl.service;

import java.nio.charset.StandardCharsets; // <-- ADD THIS IMPORT
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.forgetpassword.mbl.exception.DecryptionFailedException;

@Service
public class EncryptionService {

    @Value("${app.security.rsa.private-key}")
    private String privateKeyString;

    public String decrypt(String encryptedData) {
        try {
            String privateKeyPEM = privateKeyString
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\\s", "")
                .replace("-----END PRIVATE KEY-----", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new DecryptionFailedException("Failed to decrypt data.", e);
        }
    }
}