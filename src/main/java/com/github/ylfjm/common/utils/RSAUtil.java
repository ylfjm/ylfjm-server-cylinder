package com.github.ylfjm.common.utils;

import com.github.ylfjm.common.YlfjmException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtil {

    private static final String RSA = "RSA";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 随机生成RSA密钥对
     */
    public static KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(1024);//密钥长度，范围：512～2048，一般1024
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成RSA公钥和私钥
     *
     * @param secret 密钥字符
     */
    public static Map<String, String> generateKey(String secret) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            SecureRandom secureRandom = new SecureRandom(secret.getBytes());
            keyPairGenerator.initialize(1024, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
            Map<String, String> map = new HashMap<>();
            map.put("PUB", Base64.encodeBase64String(publicKeyBytes));
            map.put("PRI", Base64.encodeBase64String(privateKeyBytes));
            return map;
        } catch (Exception e) {
            throw new YlfjmException("生成RSA公钥和私钥失败");
        }
    }

    /**
     * 用公钥或者私钥加密
     *
     * @param data 需加密的明文数据
     * @param key  公钥或者私钥
     */
    public static String encrypt(String data, Key key) {
        try {
            // Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Cipher cipher = Cipher.getInstance("RSA");
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 传入编码数据并返回编码结果
            byte[] output = cipher.doFinal(data.getBytes(UTF8));
            return Base64.encodeBase64String(output);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用公钥或者私钥解密
     *
     * @param data 密文
     * @param key  公钥或者私钥
     */
    public static String decrypt(String data, Key key) {
        try {
            // Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] output = cipher.doFinal(Base64.decodeBase64(data.getBytes(UTF8)));
            return new String(output, UTF8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKey 公钥数据字符串
     */
    public static PublicKey loadPublicKey(String publicKey) {
        try {
            byte[] buffer = Base64.decodeBase64(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new YlfjmException("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new YlfjmException("公钥非法");
        } catch (NullPointerException e) {
            throw new YlfjmException("公钥数据为空");
        }
    }

    /**
     * 从字符串中加载私钥<br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKey
     */
    public static PrivateKey loadPrivateKey(String privateKey) {
        try {
            byte[] buffer = Base64.decodeBase64(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new YlfjmException("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new YlfjmException("私钥非法");
        } catch (NullPointerException e) {
            throw new YlfjmException("私钥数据为空");
        }
    }

    // /**
    //  * 打印公钥信息
    //  *
    //  * @param publicKey
    //  */
    // public static void printPublicKeyInfo(PublicKey publicKey) {
    //     RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
    //     System.out.println("----------RSAPublicKey----------");
    //     System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
    //     System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());
    //     System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
    //     System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
    // }
    //
    // public static void printPrivateKeyInfo(PrivateKey privateKey) {
    //     RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
    //     System.out.println("----------RSAPrivateKey ----------");
    //     System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
    //     System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());
    //     System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
    //     System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());
    // }

}
