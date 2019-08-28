package com.example.authorize.weixin.util;


import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Component
public class SignatureUtil {
	public static final String KEY_ALGORITHM = "RSA";
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	private  PublicKey pubKey=null;
	private  Cipher pubcipher=null;
	private  PrivateKey priKey=null;
	private  Cipher pricipher=null;
	
	public SignatureUtil(){
		//初始化公钥
		try {
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(decryptBASE64(readKey("rsa_public_key.pem")));
			KeyFactory keyFactory = KeyFactory.getInstance(SignatureUtil.KEY_ALGORITHM);
			pubKey = keyFactory.generatePublic(bobPubKeySpec);
			pubcipher = Cipher.getInstance(KEY_ALGORITHM); 
			pubcipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			//初始化私钥
			PKCS8EncodedKeySpec spec =new PKCS8EncodedKeySpec(decryptBASE64(readKey("pkcs8_private_key.pem")));
			KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM); 
			priKey = kf.generatePrivate(spec);  
			pricipher = Cipher.getInstance(KEY_ALGORITHM); 
			pricipher.init(Cipher.DECRYPT_MODE, priKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public String readKey(String fileName){
		String str="";
		String temp="";
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)));
			while ((str=br.readLine())!=null) {
				temp+=str;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public String signature(String text,String publicKey) throws Exception{  
		byte[] cipherText = pubcipher.doFinal(text.getBytes());  
		return encryptBASE64(cipherText);
	}
	
	public String verify(String sign,String privateKey)  throws Exception{
		byte[] plainText = pricipher.doFinal(decryptBASE64(sign));
		return new String(plainText);
	}
	public String signature(String text) throws Exception{  
		byte[] cipherText = pubcipher.doFinal(text.getBytes());  
		return encryptBASE64(cipherText);
	}
	
	public String verify(String sign)  throws Exception{
		byte[] plainText = pricipher.doFinal(decryptBASE64(sign));
		return new String(plainText);
	}
	

	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		byte[] publicKey = key.getEncoded();
		return encryptBASE64(key.getEncoded());
	}

	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		byte[] privateKey = key.getEncoded();
		return encryptBASE64(key.getEncoded());
	}

	public static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	public static String encryptBASE64(byte[] key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key);
	}

	
	
	public static Map<String, Object> initKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
}