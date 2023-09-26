package com.example.send;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class messageSend {
	
	private String hostNameUrl;
	private String requestUrl;
	private String requestUrlType;
	private String accessKey;
	private String secretKey;
	private String serviceId;
	private String method;
	private String timestamp;
	private String from;
	private String type;
	private String subject;
	
	public messageSend(String type){
		this.hostNameUrl = "https://sens.apigw.ntruss.com"; //host url
		this.requestUrl = "/sms/v2/services/"; //요청 url
		this.requestUrlType = "/messages";
		this.accessKey = "VS6yu4bhI43eNgf1CYUp";
		this.secretKey = "OhzgNDhGwr7h8AVE1jctv9rHgjBZuYg2TBv4swjs";
		this.serviceId = "ncp:sms:kr:316360759422:sms_test1";
		this.method = "POST";
		this.timestamp = Long.toString(System.currentTimeMillis());
		this.from = "01048734882";
		this.type = "sms";
		this.subject = "";
		
	}
	
	public messageSend(String type,String subject) {
		this.hostNameUrl = "https://sens.apigw.ntruss.com"; //host url
		this.requestUrl = "/sms/v2/services/"; //요청 url
		this.requestUrlType = "/messages";
		this.accessKey = "VS6yu4bhI43eNgf1CYUp";
		this.secretKey = "OhzgNDhGwr7h8AVE1jctv9rHgjBZuYg2TBv4swjs";
		this.serviceId = "ncp:sms:kr:316360759422:sms_test1";
		this.method = "POST";
		this.timestamp = Long.toString(System.currentTimeMillis());
		this.from = "01048734882";
		this.type = type;
		this.subject = subject;
	}
	public void sendSMS(String phone, String content) {
		
		requestUrl += serviceId + requestUrlType;
		String apiUrl = hostNameUrl + requestUrl;
		
		//JSON body data
		JSONObject bodyJson = new JSONObject();
		JSONObject toJson = new JSONObject();
		
		JSONArray toArr = new JSONArray();
		toJson.put("content", content);//메시지 내용
		toJson.put("to", phone); // 수신 번호 목록 + 최대 50개
		toArr.add(toJson);
		bodyJson.put("type", type);//메시지 type (SMS|LMS)
		//bodyJson.put("contentType", null); //메시지 내용 type (AD|COMM)
		bodyJson.put("countryCode", "82"); // 국가 번호
		bodyJson.put("from", from);//발신번호 , 사전 인증 등록된 번호
		bodyJson.put("subject", subject); //메시지 제목, LMS TYPE 에서 사용
		bodyJson.put("content", content); //메시지 내용
		bodyJson.put("messages", toArr);
		
		String body = bodyJson.toJSONString();
		
		System.out.println(body);
		
		try {
			URL url = new URL(apiUrl);
			
			HttpURLConnection con = (HttpURLConnection)url.openConnection(); // URL을 연결한 객체 생성
			con.setUseCaches(false); //캐싱 데이터를 받을지 말지
			con.setDoOutput(true); //쓰기 모드 지정
			//Request Header 값 셋팅 (String key, String value)
			con.setRequestProperty("content-type", "application/json; charset=utf-8");
			con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
			con.setRequestProperty("x-ncp-iam-access-key",accessKey);
			con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method,accessKey, secretKey));
			con.setRequestMethod(method); //요청 방식 선택 (POST)
			con.setDoOutput(true); //OutputStream POST 데이터를 넘겨준다
			
			//Request Body 에 Data를 담기 위해 DataOutputStream 객체 생성 
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			
			//Request body 에 Data 셋팅
			wr.write(body.getBytes());
			
			// Request body에 Data 입력
			wr.flush();
			//DataOutputStream 종료
			wr.close();
			
			// 서버로 Request 요청 (응답 코드 202 성공 , 나머지 에러)
			int responseCode = con.getResponseCode();
			BufferedReader br;
			System.out.println("responseCode" + "" + responseCode);
			if(responseCode == 202) { //정상
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { //에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = br.readLine())!= null) {
				//Request Body 정보를 불러와 String buffer 추가
				response.append(inputLine);
			}
			br.close();
			
			System.out.println(response.toString());
			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void sendLMS(String subject, String phone, String content) {
		requestUrl += serviceId + requestUrlType;
		String apiUrl = hostNameUrl + requestUrl;
		
		//JSON body data
		JSONObject bodyJson = new JSONObject();
		JSONObject toJson = new JSONObject();
		
		JSONArray toArr = new JSONArray();
		toJson.put("content", content);//메시지 내용
		toJson.put("to", phone); // 수신 번호 목록 + 최대 50개
		toArr.add(toJson);
		bodyJson.put("type", type);//메시지 type (SMS|LMS)
		//bodyJson.put("contentType", null); //메시지 내용 type (AD|COMM)
		bodyJson.put("countryCode", "82"); // 국가 번호
		bodyJson.put("from", from);//발신번호 , 사전 인증 등록된 번호
		bodyJson.put("subject", subject); //메시지 제목, LMS TYPE 에서 사용
		bodyJson.put("content", content); //메시지 내용
		bodyJson.put("messages", toArr);
		
String body = bodyJson.toJSONString();
		
		System.out.println(body);
		
		try {
			URL url = new URL(apiUrl);
			
			HttpURLConnection con = (HttpURLConnection)url.openConnection(); // URL을 연결한 객체 생성
			con.setUseCaches(false); //캐싱 데이터를 받을지 말지
			con.setDoOutput(true); //쓰기 모드 지정
			//Request Header 값 셋팅 (String key, String value)
			con.setRequestProperty("content-type", "application/json; charset=utf-8");
			con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
			con.setRequestProperty("x-ncp-iam-access-key",accessKey);
			con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method,accessKey, secretKey));
			con.setRequestMethod(method); //요청 방식 선택 (POST)
			con.setDoOutput(true); //OutputStream POST 데이터를 넘겨준다
			
			//Request Body 에 Data를 담기 위해 DataOutputStream 객체 생성 
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			
			//Request body 에 Data 셋팅
			wr.write(body.getBytes());
			
			// Request body에 Data 입력
			wr.flush();
			//DataOutputStream 종료
			wr.close();
			
			// 서버로 Request 요청 (응답 코드 202 성공 , 나머지 에러)
			int responseCode = con.getResponseCode();
			BufferedReader br;
			System.out.println("responseCode" + "" + responseCode);
			if(responseCode == 202) { //정상
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { //에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = br.readLine())!= null) {
				//Request Body 정보를 불러와 String buffer 추가
				response.append(inputLine);
			}
			br.close();
			
			System.out.println(response.toString());
			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	public static String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey)throws NoSuchAlgorithmException, InvalidKeyException{
		String space = " "; //one space
		String newLine = "\n"; //new line
		
		String message = new StringBuilder()
				.append(method)
				.append(space)
				.append(url)
				.append(newLine)
				.append(timestamp)
				.append(newLine)
				.append(accessKey)
				.toString();
		
		SecretKeySpec signingKey;
		String encodeBase64String;
		try {
			signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));

			encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
		}catch(UnsupportedEncodingException e) {
			encodeBase64String = e.toString();
		}
		return encodeBase64String;
	}
		
}


