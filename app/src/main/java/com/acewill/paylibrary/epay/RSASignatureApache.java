package com.acewill.paylibrary.epay;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
//import android.util.Base64;

public class RSASignatureApache {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
////////////////////////////////////////////////////////////////////////
	
	/**
	 * 签名算法
	 */
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	
	public static String loadPrivateKeyByFile(String path) throws Exception
	{  
		try 
		{  
			BufferedReader br = new BufferedReader(new FileReader(path));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) 
			{
				sb.append(readLine);
			}
			
			br.close();
			
			return sb.toString();
		} 
		catch (IOException e) 
		{
			throw new Exception("私钥数据读取错误");
		}
		catch (NullPointerException e) 
		{
			throw new Exception("私钥输入流为空");
		}
	}  


	/**
	* RSA签名
	* @param content 待签名数据
	* @param privateKey 商户私钥
	* @param encode 字符集编码
	* @return 签名值
	*/
	public static String sign(String content, String privateKey, String encode)
	{
        try 
        {
        	PKCS8EncodedKeySpec priPKCS8 	= new PKCS8EncodedKeySpec( Base64.decodeBase64(privateKey) );
        	//PKCS8EncodedKeySpec priPKCS8 	= new PKCS8EncodedKeySpec( Base64.decode(privateKey, Base64.DEFAULT) );
        	
        	KeyFactory keyf 				= KeyFactory.getInstance("RSA");
        	PrivateKey priKey 				= keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update( content.getBytes(encode));

            byte[] signed = signature.sign();
            
            return new String( Base64.encodeBase64(signed) );
            //return new String( Base64.encode(signed, Base64.DEFAULT) );
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
        
        return null;
    }
	
	public static String sign(String content, String privateKey)
	{
        try 
        {
        	PKCS8EncodedKeySpec priPKCS8 	= new PKCS8EncodedKeySpec( Base64.decodeBase64(privateKey) );
        	//PKCS8EncodedKeySpec priPKCS8 	= new PKCS8EncodedKeySpec( Base64.decode(privateKey,Base64.DEFAULT) );
        	KeyFactory keyf = KeyFactory.getInstance("RSA");
        	PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update( content.getBytes());
            byte[] signed = signature.sign();
            return new String( Base64.encodeBase64(signed) );
            
            //return new String( Base64.encode(signed, Base64.DEFAULT) );
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
        return null;
    }
	
	/**
	* RSA验签名检查
	* @param content 待签名数据
	* @param sign 签名值
	* @param publicKey 分配给开发商公钥
	* @param encode 字符集编码
	* @return 布尔值
	*/
	public static boolean doCheck(String content, String sign, String publicKey,String encode)
	{
		try 
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decodeBase64(publicKey);
			//byte[] encodedKey = Base64.decode(publicKey,Base64.DEFAULT);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		
			java.security.Signature signature = java.security.Signature
			.getInstance(SIGN_ALGORITHMS);
		
			signature.initVerify(pubKey);
			signature.update( content.getBytes(encode) );
		
			boolean bverify = signature.verify( Base64.decodeBase64(sign) );
			//boolean bverify = signature.verify( Base64.decode(sign,Base64.DEFAULT) );
			return bverify;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean doCheck(String content, String sign, String publicKey)
	{
		try 
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] encodedKey = Base64.decodeBase64(publicKey);
			//byte[] encodedKey = Base64.decode(publicKey,Base64.DEFAULT);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		
			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		
			signature.initVerify(pubKey);
			signature.update( content.getBytes() );
		
			boolean bverify = signature.verify( Base64.decodeBase64(sign) );
			//boolean bverify = signature.verify( Base64.decode(sign,Base64.DEFAULT) );
			return bverify;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	

}
