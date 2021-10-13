/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javasocketserver;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
//import static javasocketserver.JavaSocketServer.storedHash;
//import static javasocketserver.JavaSocketServer.storedPassword;
//import static javasocketserver.JavaSocketServer.storedUser;
//import static javasocketserver.JavaSocketServer.storedUserName;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author 30039802
 */
public class PasswordUtilities {

    static SecureRandom random;
      static      int iterations = 1024;
      static  int keyLength = 128;
    
    private static void PasswordUtilities() throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Crypto code based on the howto at https://www.baeldung.com/java-password-hashing
        // should move rng init to separate place
        random = new SecureRandom();

    }

    private static void addStoredUser() throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Crypto code based on the howto at https://www.baeldung.com/java-password-hashing
        // should move rng init to separate place
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        int iterations = 1024;
        int keyLength = 128;

        KeySpec spec = new PBEKeySpec(storedPassword.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        storedHash = hash;

        User newUser = new User();
        newUser.userName = storedUserName;
        newUser.hash = hash;
        newUser.salt = salt;
        newUser.iterations = iterations;

    }

    private static void addStoredUser() throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Crypto code based on the howto at https://www.baeldung.com/java-password-hashing
        // should move rng init to separate place
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        int iterations = 1024;
        int keyLength = 128;

        KeySpec spec = new PBEKeySpec(storedPassword.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        storedHash = hash;

        User newUser = new User();
        newUser.userName = storedUserName;
        newUser.hash = hash;
        newUser.salt = salt;
        newUser.iterations = iterations;

    }

}
