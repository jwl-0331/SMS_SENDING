package com.example.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		try {
			System.out.println(sock + ": 연결됨");
			
			fromClient = sock.getInputStream();
			
			byte[] buf = new byte[1024];
			int count; //몇바이트 받앗는지
			
			//client가 보낸것을 thread 읽어 byte 배열에 넣고  읽을게 없으면 대기
			while((count = fromClient.read(buf))!= -1) {
				final String messageFromClient =new String(buf,0,count);
				System.out.println(messageFromClient);
				if(count <= 80) {
					if(messageFromClient.contains(":") && (messageFromClient.indexOf(":") == 13)) {
						String[] msg = messageFromClient.split(":");
						String num = msg[0];
						if(telValidator(num)) {
							String[] numArray = num.split("-");
							String toPhone = "";
							for(int i = 0 ; i < 3; i++){
								toPhone += numArray[i];
							}
							String content = messageFromClient.substring(14);
							System.out.println(toPhone);
							System.out.println(content);
							System.out.println("SMS SEND");
							messageSend message = new messageSend("SMS");
							//message.sendSMS(toPhone, content);
						}else {
							System.out.println("전화 번호를 확인해주세요");
						}
					}else {
						System.out.println("JSON 형태가 아닙니다.");
					}
				}else {
					if(messageFromClient.contains(":")) {
						String[] msg = messageFromClient.split(":");
						String subject = msg[0];
						String num = msg[1];
						String content = msg[2];
						if(telValidator(num)) {
							String[] numArray = num.split("-");
							String toPhone = "";
							for(int i = 0 ; i < 3; i++){
								toPhone += numArray[i];
							}
							System.out.println(subject);
							System.out.println("LMS 전송");
							messageSend message = new messageSend("LMS", subject);
							message.sendLMS(subject,toPhone, content);
						}else {
							System.out.println("전화 번호를 확인해주세요");
						}
					}
				}
			}
		}catch(IOException e) {
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
            System.out.println(serverSocket + "서버 소켓 생성");
              
        }catch (IOException e) {
            e.printStackTrace();
        }
         
        try {
            while (true) {
                System.out.println("socket 연결 대기");
                Socket socket = serverSocket.accept();
                System.out.println("host : "+socket.getInetAddress()+" | 통신 연결 성공");
                clients.add(socket);
                 
                MainClass server = new MainClass(socket);
                server.start(); //Thread - run method 실행
         
            }
        }catch(IOException e) {
        	e.printStackTrace();
        }
	}
}
	
