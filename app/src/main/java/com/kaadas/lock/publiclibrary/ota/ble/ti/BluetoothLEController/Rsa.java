package com.kaadas.lock.publiclibrary.ota.ble.ti.BluetoothLEController;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author alun (http://alunblog.duapp.com)
 * @version 1.0
 */
public class Rsa {
	
	private static final String RSA_PUBLICE = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3//sR2tXw0wrC2DySx8vNGlqt3Y7ldU9+LBLI6e1KS5lfc5jlTGF7KBTSkCHBM3ouEHWqp1ZJ85iJe59aF5gIB2klBd6h4wrbbHA2XE1sq21ykja/Gqx7/IRia3zQfxGv/qEkyGOx+XALVoOlZqDwh76o2n1vP1D+tD3amHsK7QIDAQAB";
	private static final String ALGORITHM = "RSA";
	
	
	/**
	 * 得到公钥
	 *
	 * @param algorithm
	 * @param bysKey
	 * @return
	 */
	private static PublicKey getPublicKeyFromX509(String algorithm,
												  String bysKey) throws NoSuchAlgorithmException, Exception {
		byte[] decodedKey = Base64.decode(bysKey, Base64.DEFAULT);
		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(x509);
	}
	
	
	/**
	 * 使用公钥加密
	 *
	 * @param content
	 * @return
	 */
	public static String encryptByPublic(String content) {
		try {
			PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);
			byte plaintext[] = content.getBytes("UTF-8");
			byte[] output = cipher.doFinal(plaintext);
			String s = new String(Base64.encode(output, Base64.DEFAULT));
			return s;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 使用公钥解密
	 *
	 * @param content 密文
	 * @return 解密后的字符串
	 */
	public static String decryptByPublic(String content) {
		try {
			PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, pubkey);
			InputStream ins = new ByteArrayInputStream(Base64.decode(content, Base64.DEFAULT));
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			byte[] buf = new byte[128];
			int bufl;
			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;
				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}
				writer.write(cipher.doFinal(block));
			}
			return new String(writer.toByteArray(), "utf-8");
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 加密
	 *
	 * @param content  需要加密的内容
	 * @param password 加密密码
	 * @return
	 */
	public static byte[] encrypt(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content;//content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加密
	 *
	 * @param content  需要加密的内容
	 * @param password 加密密码
	 * @return
	 */
	public static byte[] encryptAES(byte[] content, byte[] password) {
		try {
			if (password == null) {
				System.out.print("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (password.length != 16) {
				System.out.print("Key长度不是16位");
				return null;
			}
			// SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			SecretKeySpec key = new SecretKeySpec(password, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			// byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (Exception e) {
			Log.e("walter", "加密异常==" + e.toString());
		}
		return null;
	}
	/**
	 * 加密
	 *
	 * @param content  需要加密的内容
	 * @param password 加密密码
	 * @return
	 */
	public static byte[] encrypt2(byte[] content, byte[] password) {
		try {
			// SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			SecretKeySpec key = new SecretKeySpec(password, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			// byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 解密
	 *
	 * @param content  待解密内容
	 * @param password 解密密钥
	 * @return
	 */
	public static byte[] decrypt(byte[] content, byte[] password) {
		try {
			//SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			SecretKeySpec key = new SecretKeySpec(password, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			// byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; //
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String toHexString(byte[] value) {
		StringBuffer mToHexString = new StringBuffer();
		for (int i = 0; i < value.length; i++) {
			mToHexString.append(Integer.toHexString((value[i] & 0xFF)));
			mToHexString.append(" ");
		}
//		Log.e("walter", "读取蓝牙特征值 mToHexString= " + mToHexString);
		return mToHexString.toString();
	}

	public static int byteArrayToInt(byte[] b) {
		return   b[3] & 0xFF |
				(b[2] & 0xFF) << 8 |
				(b[1] & 0xFF) << 16 |
				(b[0] & 0xFF) << 24;
	}
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	public static  String byteTo10(byte[] value) {
		StringBuffer mToHexString = new StringBuffer();
		for (int i = 0; i < value.length; i++) {
			mToHexString.append(Integer.toHexString((value[i] & 0xFF)));
		}
		Log.e("walter", "读取蓝牙特征值 mToHexString= " + mToHexString);
		return mToHexString.toString();
	}
	public static final byte[] hex2byte(String hex)
			throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] arr = hex.toCharArray();
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			String swap = "" + arr[i++] + arr[i];
			int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = new Integer(byteint).byteValue();
		}
		return b;
	}
	public static int bytes2Int(byte[] bt) {
		byte[] b = new byte[4];
		b[3] = bt[0];
		b[2] = bt[1];
		b[1] = bt[2];
		b[0] = bt[3];
		int result = 0;
		for (int i = 0; i < b.length; i++) {
			int tmpVal = (b[i] << (8 * (3 - i)));
			switch (i) {
				case 0:
					tmpVal = tmpVal & 0xFF000000;
					break;
				case 1:
					tmpVal = tmpVal & 0x00FF0000;
					break;
				case 2:
					tmpVal = tmpVal & 0x0000FF00;
					break;
				case 3:
					tmpVal = tmpVal & 0x000000FF;
					break;
			}
			result = result | tmpVal;
		}
		return result;
	}
	public static byte[] long2Bytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}
	/**
	 * 把int转换成byte数组
	 *
	 * @return 返回的byte数组
	 */
	public static byte[] int2BytesArray(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (n >> (24 - i * 8));
		}
		byte[] transitionByte=new byte[4];
		transitionByte[3]=b[0];
		transitionByte[2]=b[1];
		transitionByte[1]=b[2];
		transitionByte[0]=b[3];
		return transitionByte;
	}


}

