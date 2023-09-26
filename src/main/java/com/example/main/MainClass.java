package com.example.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.send.messageSend;

public class MainClass extends Thread{
	final static int SERVER_PORT =9999;
	private Socket sock;
	private static ArrayList<Socket> clients = new ArrayList<Socket>(5);
	
	public MainClass(Socket sock) {
		this.sock = sock;
	}
	
	public void remove(Socket socket) {
		for(Socket s : MainClass.clients) {
			if(socket == s) {
				MainClass.clients.remove(socket);
				break;
			}
		}
	}
	
	public void run() {
		//thread 할일 기술
		InputStream fromClient = null;
		OutputStream fromServer = null;
		
		try {
			System.out.println(sock + ": Connected");
			
			fromClient = sock.getInputStream();
			fromServer = sock.getOutputStream();
			PrintWriter writer = new PrintWriter(fromServer, true); //Buffered Reader 에 위 InputStream을 담아 사용
			//Server -> Client
			
			
			byte[] buf = new byte[1024];
			int count; //몇바이트 받앗는지
			
			//client가 보낸것을 thread 읽어 byte 배열에 넣고  읽을게 없으면 대기
			while((count = fromClient.read(buf))!= -1) {
				final String messageFromClient =new String(buf,0,count);
				
				String jsonStr = "{" + messageFromClient + "}";
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(jsonStr);
				JSONObject bodyJson = (JSONObject)obj;
				
				System.out.println(bodyJson);
				
				String tel =  (String) bodyJson.get("tel");
				String content = (String) bodyJson.get("content");
				String subject = (String) bodyJson.get("subject");
				if(tel == null | tel== "") {
					
					writer.println("Input Phone number");
				}else {	
					if(content.getBytes().length <= 80) {
						if(telValidator(tel)) {
							String[] numArray = tel.split("-");
							String toPhone = "";
							for(int i = 0 ; i < 3; i++){
								toPhone += numArray[i];
							}
							System.out.println(toPhone);
							System.out.println(content);
							writer.println("SMS SEND");
							messageSend message = new messageSend("SMS");
							message.sendSMS(toPhone, content);
							
						}else {
							writer.println("Check Phone num.");
						}
					}else {
						if(subject == null | subject == "") {
							writer.println("Input Subject");
						}else {
							if(telValidator(tel)) {
								String[] numArray = tel.split("-");
								String toPhone = "";
								for(int i = 0 ; i < 3; i++){
									toPhone += numArray[i];
								}
								writer.println("LMS SEND");
								messageSend message = new messageSend("LMS", subject);
								//message.sendLMS(subject,toPhone, content);
							}else {
								writer.println("Check phone num.");
							}
						}
					}
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(sock != null) {
					sock.close();
					//접속후 나가버린 클라이언트 Array 제거
					remove(sock);
				}
				fromClient = null;
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean telValidator(String number) {
		 Pattern pattern = Pattern.compile("\\d{3}-\\d{4}-\\d{4}");
		 Matcher matcher = pattern.matcher(number);
		 if (matcher.matches()) {
            System.out.println("Valid phone number: " + number);
            return true;
		 } else {
            System.out.println("Invalid. Not the form XXX-XXXX-XXX: " + number);
            return false;
		 }
	}
	
	
	public static void main(String[] args) {
        
        ServerSocket serverSocket =null;
         
        try {
            serverSocket =new ServerSocket(SERVER_PORT);
            System.out.println(serverSocket + "Create Server Socket");
              
        }catch (IOException e) {
            e.printStackTrace();
        }
         
        try {
            while (true) {
                System.out.println("socket Listen");
                Socket socket = serverSocket.accept();
                System.out.println("host : "+socket.getInetAddress()+" | Connect Success");
                clients.add(socket);
                 
                MainClass server = new MainClass(socket);
                server.start(); //Thread - run method 실행
         
            }
        }catch(IOException e) {
        	e.printStackTrace();
        }
	}
}
	
