package com.acewill.paylibrary.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2015050800065426";//不用管
	public static String MCH_NAME = "";
	
	//开饭(茶里)
//	public static String PID = "2088811293418234";
//	public static String APPID = "2015062700147170";// 
//	public  static String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMQJm30oHFiA/LwICM9ciAx19gumhyAHSBWABB1RRTz08zgra9k4s/o0nxLi6A75T+oPM/SKYYjesOR1Nf9TCDBU0Q53xy962dqOzkx31EhYXL/2HqHGeqw8ZWPJTqXiuaq0wFAafuDS158tT4PzdeuAvmrDy4mP6JArciEf6/EpAgMBAAECgYBC65SssNicTcv41DVNtCStPEd1yq8t+85bqIu/BlNAb83nSstSJ80oiHYVGGhnVCw3lmECBYsuEfcIxgpc0pdkbsx8DQZjv6+MgtdExV2MrLJFl4sxF7dNRYnC5nPxAY/btvCT0F0aYVoDQqMYieovEvdq8MLqiT12L3eLbNZEAQJBAPQc9SPbqMaBDtLS8k9qtTKlKcCyY/ZtDSm9P+5cD4YWY2GZ2T70+UCMKqt6OeGZDftvMIjbuzwPgo5igIIqYyECQQDNlVqI8PbSJvLLutGdPZCmZJOrpEIsamOPcOOLOhawHXlSL4qTBrEqtZebQVHLKRBEps/yXh+R9bZ0Nou9dNUJAkAF5XkqX5DpEo74Gq5NqscrrP+gLu7pMeMigfa8XC7nzzyhS+UKd0JaA8i4EaC6SUckLqBefeg84CcIJ2rxOfLhAkEAiNaOynPJdhwio+SXvVfLPlXXzPmqajHuTv3PF0705wjXRhMK484igtEYNmkF7npIr8lwxFxOGzM33Kap4NMxMQJACQluQDl1o0kJg7inaoCzshlKIPMT6SO+6a8t30GNW4bHeG7hldlxt4HMLrQcJ+1jvV7heMB2dTMiVwDdhATmPQ==";
	
	//深圳测试账号
//	public static String PID = "";
//	public static String APPID = "2016030101175068";
//	public static String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKOU+PitpG3Evs5SUVb6BQ/Cyw3Ef2ci1nKAIE/BYKlZ32dhJNGv3vooVf/Vja58rqO8LPRY1N00ZYtEw3P6npWLMW4QjPcCc/B0uJhKioxB5mDkxQ2MVf0G21HPesCcLilMOjhVt3AAB6OJoPn7EErTFKgWMPnEpuZXfen4MG4BAgMBAAECgYAxg30c4Ipdw2ix0M7YEdOIYsDNiQW7NRtOCyQ8n97fQ9nQU+IuKhrHA4CMcJpzD0BZRTMiPuUnk52M2yKCL4DibPZdplQ8G3TmRJiCXIzopAoxwnVCSwJCIQp7qR1T1d/6FszlrwnzPXmz+btGK5ALQUJsUnMp3pyy8KGt5JGgwQJBANcoT6c319Ipf6qfVLSvoqPYQah7yTkFN5+1dolHL+UP4QlkaWP3t3ERLVSOYOAl8ttog2HyBVWqeA7JfYGlmmkCQQDColI3foQZI4j3pNjR8Co4rJmlRQLrJpURtSwhXsXAwsj3n74KFHPlvOYaPVQQhiMkhacS5i0NK54vCpJmrNPZAkEAptC+USvezTcXoLY/+odiVh5JadPvw6Hj6pPK/8yNuc+B7sJHZBafx65FsxVgzukdTjfOBZabxDuTMgPOp0I6YQJBAJK1uDOMchZg6sWAxM66sZi2wboKIwENvB/06KbewUFjkgjVqHIAqLvrf6cXw14RPjwxYpakWiErV7ktxt0OOUECQH1E5jPbStwhb1RAZ2v/Z5CC9jezximA6eoDW3g3nzjlqDJ0VnKfYCVFkk9jE4Cx++22XQKo2j1jBbpRbLBEv10=";

	public static String PID = "";
	public static String APPID = "";
	public static String key = "";


	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";

	// 签名方式 不需修改
	public static String sign_type = "MD5";

}
