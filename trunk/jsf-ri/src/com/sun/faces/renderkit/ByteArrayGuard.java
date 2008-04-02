/*
 * $Id: ByteArrayGuard.java,v 1.7 2005/08/22 22:10:17 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * https://javaserverfaces.dev.java.net/CDDL.html or
 * legal/CDDLv1.0.txt. 
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.    
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.faces.renderkit;

import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.faces.context.FacesContext;
import com.sun.faces.util.Util;

/**
 * This utility class provides services to encrypt or decrypt a byte array.
 * The algorithm used to encrypt byte array is 3DES with CBC
 * The algorithm used to create the message authentication code (MAC) is SHA1
 * 
 * Original author Inderjeet Singh, J2EE Blue Prints Team. Modified to suit JSF
 * needs. 
 */
public class ByteArrayGuard {
    public static final int DEFAULT_KEY_LENGTH = 24;
    public static final int DEFAULT_MAC_LENGTH = 20;
    public static final int DEFAULT_IV_LENGTH = 8;
    
    public static final String SESSION_KEY_FOR_PASSWORD = 
            "com.sun.faces.clientside-state.password-key";
    public static final int DEFAULT_PASSWORD_LENGTH = 24;
    
    // Log instance for this class
    private static final Logger logger = 
            Util.getLogger(Util.FACES_LOGGER + Util.RENDERKIT_LOGGER);
    
    /** 
     * @param ps the password strategy to create password for encryption and decryption
     * uses default values for the length of the encryption key, MAC key, and 
     * the initialization vector
     * @see DEFAULT_KEY_LENGTH
     * @see DEFAULT_MAC_LENGTH
     * @see DEFAULT_IV_LENGTH
     */
    public ByteArrayGuard() {
        this(DEFAULT_KEY_LENGTH, DEFAULT_MAC_LENGTH, DEFAULT_IV_LENGTH);
    }
    
    /**
     * @param keyLength the length of the key used for encryption
     * @param macLength the length of the message authentication used 
     * @param ivLength length of the initialization vector used by the block cipher
     * @param 
     */
    public ByteArrayGuard(int keyLength, int macLength, int ivLength) {
        this.keyLength = keyLength;
        this.macLength = macLength;
        this.ivLength = ivLength;
        
        // generate random password in memory
        this.password = getRandomString(DEFAULT_PASSWORD_LENGTH);
    }
    
    /**
     * Encrypts the specified plaindata using the specified password. It also
     * stores the MAC and the IV in the output. The 20-byte MAC is stored
     * first, followed by the 8-byte IV, followed by the encrypted
     * contents of the file.
     * @param context FacesContext for this request
     * @param plaindata The plain text that needs to be encrypted
     * @return The encrypted contents
     */
    public byte[] encrypt(FacesContext context, byte[] plaindata) throws IOException {
        try {
            // generate a key that can be used for encryption from the 
            // supplied password
            byte[] rawKey = convertPasswordToKey(getPasswordToSecureState(context));
            // choose block encryption algorithm
            Cipher cipher = getBlockCipherForEncryption(rawKey);
            // encrypt the plaintext
            byte[] encdata = cipher.doFinal(plaindata);
            // choose mac algorithm
            Mac mac = getMac(rawKey);
            // generate MAC for the initialization vector of the cipher
            byte[] iv = cipher.getIV();
            mac.update(iv);
            // generate MAC for the encrypted data
            mac.update(encdata);
            // generate MAC
            byte[] macBytes = mac.doFinal();
            
            // concat byte arrays for MAC, IV, and encrypted data
            // Note that the order is important here. MAC and IV are
            // of fixed length and need to appear before the encrypted data
            // for easy extraction while decrypting.
            byte[] tmp = concatBytes(macBytes, iv);
            byte[] securedata = concatBytes(tmp, encdata);
            return securedata;
        } catch (Exception e) {
             if (logger.isLoggable(Level.SEVERE)) {
                 logger.log(Level.SEVERE,e.getMessage(), e.getCause());
             }
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Decrypts the specified byte array using the specified password, and
     * generates an inputstream from it. The file must be encrypted by the
     * above method for encryption. The method also verifies the MAC. It
     * uses the IV present in the file for decryption.
     * @param context Faces Context for this request
     * @param securedata The encrypted data (including mac and initialization 
     * vector) that needs to be decrypted
     * @return A byte array containing the decrypted contents
     */
    public byte[] decrypt(FacesContext context, byte[] securedata) throws IOException {
        try {
            // Extract MAC
            byte[] macBytes = new byte[macLength];
            System.arraycopy(securedata, 0, macBytes, 0, macBytes.length);
            // Extract initialization vector used for encryption
            byte[] iv = new byte[ivLength];
            System.arraycopy(securedata, macBytes.length, iv, 0, iv.length);
           
            // Extract encrypted data
            byte[] encdata = new byte[securedata.length - macBytes.length - iv.length];
            System.arraycopy(securedata, macBytes.length + iv.length, encdata, 0, encdata.length);
            
            // verify MAC by regenerating it and comparing it with the received value
            byte[] rawKey = convertPasswordToKey(getPasswordToSecureState(context));
            Mac mac = getMac(rawKey);
            mac.update(iv);
            mac.update(encdata);
            byte[] macBytesCalculated = mac.doFinal();
            if (Arrays.equals(macBytes, macBytesCalculated)) {
                // decrypt data only if the MAC was valid
                Cipher cipher = getBlockCipherForDecryption(rawKey, iv);
                byte[] plaindata = cipher.doFinal(encdata);
                return plaindata;
            } else {
                throw new IOException("Could not Decrypt Secure View State, passwords did not match.");
            }
        } catch (Exception e) {
            if (logger.isLoggable(Level.SEVERE)) {
                 logger.log(Level.SEVERE,e.getMessage(), e.getCause());
            }
            throw new RuntimeException(e);
        }
    }
    
    /**
     * This method provides a password to be used for encryption/decryption of 
     * client-side state.
     * <p>
     * We have two password options.  The first is the 'application' scoped password
     * that is used by all requests.  In the case of an application restart, where
     * session state is persisted, we MUST use the legacy password from the session
     * instead of our application password.
     * </p>
     * <p>
     * Theoretically after a restart, you could have multiple passwords being used
     * at once: the application's and legacy sessions'.
     * </p>
     * Use cases:
     * <ol>
     *  <li>Stateless to Stateful - both will use the same 'application' password,
     *      so no problems</li>
     *  <li>Stateful to Stateless - invalidating the session will cause the application
     *      password to be used.  Unless the server restarted, decrypting will still work.
     *      </li>
     *  <li>Stateless Restart - FIXME, application loses its password, and a new one will
     *      be generated for all succeeding requests.  I suggest adding an init-param to
     *      web.xml to define the application password, if null, generate one.</li>
     *  <li>Stateful Restart - you will continue to use the key stored in your session</li>
     * </ol>
     */
    private String getPasswordToSecureState(FacesContext context) {
        // default is to use application scoped password
        String statePwd = this.password;
        
        // check if there is a session available
        Object sessionObj = context.getExternalContext().getSession(false);
        
        // if there is a session....
        if ( sessionObj != null ) {
            Map sessionMap = context.getExternalContext().getSessionMap();
            
            // try to get a legacy password from the session to use
            statePwd = (String) sessionMap.get(SESSION_KEY_FOR_PASSWORD);
            
            // if no legacy password, use our application password
            // and store it for session persistence
            if (statePwd == null) {
                statePwd = this.password;
                sessionMap.put(SESSION_KEY_FOR_PASSWORD, statePwd); 
            }
        }
        return statePwd;
    }
    
    /**
     * This method converts the specified password into a key in a
     * deterministic manner. The key is then usable for creating ciphers
     * and MACs.
     * @return a byte array containing a key based on the specified
     * password. The length of the returned byte array is KEY_LENGTH.
     */
    private byte[] convertPasswordToKey(byte[] password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] seed = md.digest(password);
            
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);
            
            byte[] rawkey = new byte[keyLength];
            random.nextBytes(rawkey);
            return rawkey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /** A convenience alias to the above method which takes a string as
     * the password.  
     */
    private byte[] convertPasswordToKey(String password) {
        return convertPasswordToKey(password.getBytes());
    }
    
    /** @return a 3DES block cipher to be used for encryption based on the
     * specified key
     * @param rawKey must be 24 bytes in length. 
     */
    private Cipher getBlockCipherForEncryption(byte[] rawKey) {
        try {
            SecretKeyFactory keygen = SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec keyspec = new DESedeKeySpec(rawKey);
            Key key = keygen.generateSecret(keyspec);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            byte[] iv = new byte[ivLength];
            getPRNG().nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec, getPRNG());
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Cipher getBlockCipherForDecryption(byte[] rawKey, byte[]
            iv) {
        try {
            SecretKeyFactory keygen = SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec keyspec = new DESedeKeySpec(rawKey);
            Key key = keygen.generateSecret(keyspec);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec, getPRNG());
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Mac getMac(byte[] rawKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec key = new SecretKeySpec(rawKey, 0, macLength, "HmacSHA1");
            mac.init(key);
            return mac;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /** 
     * Generates a cryptographically random string
     * @param size the desired length of the string 
     */
    static String getRandomString(int size) {
        byte[] data = new byte[size];
        getPRNG().nextBytes(data);
        return new String(data);
    }
    
    private static int getRandomInt() {
        byte[] data = new byte[4];
        getPRNG().nextBytes(data);
        return data[0] + data[1] * 256 + data[2] * 65536 + data[3] * 16777216;
    }
    
    private static SecureRandom getPRNG() {
        try {
            if (prng == null) {
                prng = SecureRandom.getInstance("SHA1PRNG");
            }
            return prng;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String getHexString(byte[] b) {
        StringBuffer buf = new StringBuffer(b.length);
        for (int i = 0; i < b.length; ++i) {
            byte2hex(b[i], buf);
        }
        return buf.toString();
    }
    
    /**
     * This method concatenates two byte arrays
     * @return a byte array of array1||array2
     * @param array1 first byte array to be concatenated
     * @param array2 second byte array to be concatenated
     */
    private static byte[] concatBytes(byte[] array1, byte[] array2) {
        byte[] cBytes = new byte[array1.length + array2.length];
        try {
            System.arraycopy(array1, 0, cBytes, 0, array1.length);
            System.arraycopy(array2, 0, cBytes, array1.length, array2.length);
        } catch(Exception e) {
            throw new RuntimeException(e);              
        }
        return cBytes;
    }
    
    /**
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8','9', 
                'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
    
    private final int keyLength;
    private final int macLength;
    private final int ivLength;
    private final String password;
    private static SecureRandom prng = null;
}
