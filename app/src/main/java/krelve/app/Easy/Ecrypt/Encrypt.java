package krelve.app.Easy.Ecrypt;

import java.net.URLEncoder;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Encrypt {

	private static String str_pwd;
	public static byte[] encrypt(String paramString1, String paramString2) throws Exception {
		Cipher localCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec localDESKeySpec = new DESKeySpec(paramString2.getBytes("UTF-8"));
		localCipher.init(1, SecretKeyFactory.getInstance("DES").generateSecret(localDESKeySpec),
				new IvParameterSpec(paramString2.getBytes("UTF-8")));
		return localCipher.doFinal(paramString1.getBytes("UTF-8"));
	}

	public static String binary2Hex(byte[] paramArrayOfByte) {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0;; i++) {
			if (i >= paramArrayOfByte.length)
				return localStringBuilder.toString();
			localStringBuilder.append(String.valueOf("0123456789ABCDEF".charAt((0xF0 & paramArrayOfByte[i]) >> 4)));
			localStringBuilder.append(String.valueOf("0123456789ABCDEF".charAt(0xF & paramArrayOfByte[i])));
		}
	}
	
	
//	public static void main(String[] args) {
//		Scanner in =null;
//		try {
//			String result;
//			in = new Scanner(System.in);
//			str_pwd = in.nextLine();
//			result = binary2Hex(encrypt(URLEncoder.encode(str_pwd, "utf-8").toLowerCase(), "synjones")).toUpperCase();
//
//			System.out.println(result);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally {
//			in.close();
//		}
//	}

}
