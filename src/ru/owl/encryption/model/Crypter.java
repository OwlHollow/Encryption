package ru.owl.encryption.model;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Random;

public class Crypter {
    private StringBuilder value;
    private Random random = new Random();
    public static enum Mode {Encrypt, Decrypt};
    public static enum Algorithm {Caesar, Parallel, AES, RSA};
    
    public Crypter(){
        value = new StringBuilder("Введите текст...");
    };

    public String generateKey(int length){
        byte[] key = new byte[length];

        for(int i = 0; i < length; i++){
            key[i] = getRandomInRange(33, 126);
        }

        return new String(key);
    }

    public String encryptText(StringBuilder value, String key){
        this.value = value;
        
        int keyIndex = 0,
            charCode = 0;
        for(int i = 0; i < value.length(); i++){
            if(keyIndex >= key.length())
                keyIndex = 0;
            charCode = (int)value.charAt(i) + (int)key.charAt(keyIndex++);
            if(charCode == 127)
                charCode = (int)'*';
            value.setCharAt(i, (char)(charCode));
        }
        
        return value.toString();
    }
    
    public String decryptText(StringBuilder value, String key){
        this.value = value;
        
        int keyIndex = 0,
            charCode = 0;
        for(int i = 0; i < value.length(); i++){
            if(keyIndex >= key.length())
                keyIndex = 0;
            charCode = (int)value.charAt(i) - (int)key.charAt(keyIndex++);
            if(charCode == 127)
                charCode = (int)'*';
            value.setCharAt(i, (char)(charCode));
        }
        
        return value.toString();
    }

    public byte[] doWork (File inputFile, String key, Algorithm alg, Mode mode) throws IOException {
        byte[] inputFileBytes = IOWorker.getFileBytes(inputFile),
                keyBytes = key.getBytes();

        switch (alg){
            case Caesar:
                return CaesarAlg(inputFileBytes, keyBytes, mode);
            case Parallel:
                return parallelAlg(inputFileBytes, keyBytes, mode);
            case AES:
                return AESAlg(inputFileBytes, keyBytes, mode);
            case RSA:
                return RSAAlg(inputFileBytes, keyBytes, mode);
            default:
                return null;
        }
    }

    private byte[] parallelAlg(byte[] fileBytes, byte[] keyBytes, Mode mode){
        int keyIndex = 0;

        for(int i = 0; i < fileBytes.length; i++) {
            if (keyIndex >= keyBytes.length) {
                keyIndex = 0;
            }

            switch (mode){
                case Encrypt:
                    fileBytes[i] += keyBytes[keyIndex++];
                    break;
                case Decrypt:
                    fileBytes[i] -= keyBytes[keyIndex++];
                    break;
            }
        }

        return fileBytes;
    }

    private byte[] AESAlg(byte[] fileBytes, byte[] keyBytes, Mode mode){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 16);

            //SecureRandom random = SecureRandom.getInstanceStrong();
            byte[] rnd = {-45, -64, 21, 103, -58, -85, 32, 11, 127, -54, -2, 81, 32, -11, 101, 23};
            //random.nextBytes(rnd);
            IvParameterSpec ivSpec = new IvParameterSpec(rnd);

            Key key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            switch (mode){
                case Encrypt:
                    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
                    break;
                case Decrypt:
                    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
                    break;
            }

            fileBytes = cipher.doFinal(fileBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

    private byte[] RSAAlg(byte[] fileBytes, byte[] keyBytes, Mode mode) {
        return null;
    }

    private byte[] CaesarAlg(byte[] fileBytes, byte[] keyBytes, Mode mode) {
        byte offset = Byte.decode(new String(keyBytes));
        for(int i = 0; i < fileBytes.length; i++) {
            switch (mode){
                case Encrypt:
                    fileBytes[i] += offset;
                    break;
                case Decrypt:
                    fileBytes[i] -= offset;
                    break;
            }
        }

        return fileBytes;
    }

    private byte getRandomInRange(int from, int to){
        byte result;
        result = (byte)(random.nextInt((to - from) + 1) + from);

        return result;
    }
}