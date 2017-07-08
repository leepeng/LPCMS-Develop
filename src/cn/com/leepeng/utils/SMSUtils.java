package cn.com.leepeng.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * 
 * Class Description: SMSUtils.java
 *
 * @author Leepeng <a href="http//www.leepeng.com.cn/">李鹏</a>
 * @date 2016-3-5 下午01:23:07
 * E-mail:leepeng@leepeng.com.cn
 */
public class SMSUtils {
	/**
	 * 短信验证码注册模板
	 */
	public static final String REGISTER_TEMPLATE_ID = "20120046";
	//public static final String REGISTER_TEMPLATE_ID = "3840228";
	
	/**
	 * 短信验证码找回密码模板
	 */
	public static final String FORGET_PASSWORD_TEMPLATE_ID = "20120047";
	
	//开发者主账号ID（ACCOUNT SID）。由32个英文字母和阿拉伯数字组成的开发者账号唯一标识符。
	//private static final String ACCOUNT_SID = "8c5717af03ad4c2cb6085f4533f8b08d";  
	private static final String ACCOUNT_SID = "d95950e3b49d45e597670beebda3bad5";  
	
	//private static final String AUTH_TOKEN = "16a59400c07347358541c215c74b1ca0";
	private static final String AUTH_TOKEN = "815dede4fa4d402eb1e7582e749bd0a5";
	
	//应用ID。创建应用提交后由系统自动分配。
	private static String APP_Id = "e6a0595b337f48c2b8c26bf21c253164";
	//private static String APP_Id = "4be7753afa5743b5869e273c0334cd4b";
	
	//短信接收端手机号码集合。用英文逗号分开，每批发送的手机号数量不得超过100个。
	private static String to;
	
	//内容数据。用于替换模板中{数字}，若有多个替换内容，用英文逗号隔开。
	private static String param;
	
	//时间戳。当前系统时间（24小时制），格式"yyyyMMddHHmmss"。时间戳有效时间为5分钟。
	private String timestamp;
	
	//签名。MD5(ACCOUNT SID + AUTH TOKEN + timestamp)。共32位（小写）。(注意：MD5中的内容不包含”+”号。)
	private String sig;
	
	//响应数据类型，JSON 或 XML 格式。默认为JSON。
	private String respDataType;
	
	public static void sendSMS(String phones,String templateId){
		if(StringUtils.isNotEmpty(phones)){
			return;
		}

		
		OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			StringBuffer url = new StringBuffer("https://api.qingmayun.com/20150822/SMS/templateSMS?");
			url.append("accountSid").append("=").append(ACCOUNT_SID).append("&")
				.append("appId").append("=").append(APP_Id).append("&")
				.append("templateId").append("=").append(templateId).append("&")
				.append("to").append("=").append(phones).append("&")
				.append("param").append("=").append(param).append("&")
				.append("timestamp").append("=").append(timestamp).append("&")
				.append("sig").append("=").append(MD5Utils.getMD5String(SMSUtils.ACCOUNT_SID+SMSUtils.AUTH_TOKEN+timestamp));
			
			URL smsurl = new URL(url.toString());
			URLConnection urlConnection = smsurl.openConnection();
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection)urlConnection;
			httpsURLConnection.setDoOutput(true);
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(url.toString().length()));
			httpsURLConnection.setSSLSocketFactory(ssf);
			
			
			 outputStream = httpsURLConnection.getOutputStream();
	        outputStreamWriter = new OutputStreamWriter(outputStream);
	        
	        outputStreamWriter.write(url.toString().toString());
	        outputStreamWriter.flush();
	        
	        if (httpsURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpsURLConnection.getResponseCode());
            }
            
            inputStream = httpsURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
			System.out.println(resultBuffer);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (reader != null) {
                try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (inputStreamReader != null) {
                try {
					inputStreamReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (inputStream != null) {
                try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		}
	}
	public static void main(String[] args) {
//		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//		String md5String = MD5Utils.getMD5String(SMSUtils.ACCOUNT_SID+SMSUtils.AUTH_TOKEN+timestamp);
//		System.out.println(md5String);
		sendSMS("18301593474", SMSUtils.REGISTER_TEMPLATE_ID);
	}
}
