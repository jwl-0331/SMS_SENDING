# SMS_SENDING
네이버 SENS Cloud Service 를 이용하여 SMS 문자 보내기 구현하기

프로젝트명 : 문자 전송 서버 개발				          작성자 : 이재원
작성일 : 2023/09/26

-파일명 : SMS_SEND.jar
-파일 실행 : CMD -> java -jar SMS_SEND.jar

-프로젝트 설명
TCP 소켓 통신 기반 서버 생성, Client 해당 Server Port로 접속하여 서버에 JSON 형식으로 메시지를 보낼 경우 전화번호와 내용을 확인하여 NAVER SENS API 연동을 통해 해당 전화번호로 문자를 보내는 프로그램.
발신 번호는 해당 Cloud에 등록한 번호로만 발신이 가능 “010-4873-4882”으로 고정
-패키지 및 클래스 정보
 
-build gradle : JSON 형태 데이터 가공을 위한 라이브러리 Json dependencies 선언
dependencies {
implementation 'org.springframework.boot:spring-boot-starter'
developmentOnly 'org.springframework.boot:spring-boot-devtools'
testImplementation 'org.springframework.boot:spring-boot-starter-test'
implementation 'org.json:json:20190722'
implementation 'com.googlecode.json-simple:json-simple:1.1.1'
}

-MainClass
ServerSocket 생성 및 소켓 통신, 메시지 가공

-SERVER SOCKET
 
PORT : 9999
서버 소켓은 클라이언트가 Connect 하면 Thread를 생성하면서 run() 함수를 실행
Run()  - 
Connect 된 후 Client 가 보낸 데이터를 thread가 byte 단위로 읽어온다. 읽을게 없으면 대기상태.
읽어온 byte를 기준으로  80 byte 이상은 LMS 형 문자 발송 그 이하는 SMS 형 문자 발송
Client가 보내는 데이터는 
CASE : SMS
“[XXX-XXXX-XXXX]:[CONTENT]” 형식의 데이터 이외의 형식은 처리 하지 않고 오류 메시지를 띄운다.
CASE : LMS
“[SUBJECT]:XXX-XXXX-XXXX]-[CONTENT]” 형식의 데이터 이외의 형식은 처리 하지 않고 오류 메시지를 띄운다.

-messageSend
HttpURLConnection 을 통해 Naver SENS Cloud API와의 연동, 데이터 전송, 암호화(makeSignature()) 함수를 포함하는 클래스

-요청 URL : POST https://sens.apigw.ntruss.com/sms/v2/services/{serviceId}/messages
apiUrl = "https://sens.apigw.ntruss.com/sms/v2/services/ncp:sms:kr:316360759422:sms_test1/messages
-	Naver SENS Cloud 콘솔 화면에서 프로젝트 생성 후 해당 serviced 를 포함하여 apiURL을 생성하여 URL Connection을 시도
-	accessKey, secretKey는 해당 마이 페이지에서 발급 받아 Connection API Header 데이터로 이용
-	Content-Type: application/json; charset=utf-8
-	x-ncp-apigw-timestamp: {Timestamp}
-	x-ncp-iam-access-key: {Sub Account Access Key}
-	x-ncp-apigw-signature-v2: {API Gateway Signature}



-요청 BODY (JSON)
{
    "type":"(SMS | LMS | MMS)",
    "contentType":"(COMM | AD)",
    "countryCode":"string",
    "from":"string",
    "subject":"string",
    "content":"string",
    "messages":[
        {
            "to":"string",
            "subject":"string",
            "content":"string"
        }
    ],
    "files":[
        {
             "fileId": "string"
        }
    ],
    "reserveTime": "yyyy-MM-dd HH:mm",
    "reserveTimeZone": "string"
}
-	해당 변수 값들 MainClass에서 가공 후 messageSend 클래스 객체 생성을 하여 전송
-	서버로 Request 요청 (응답 코드 202 성공 , 나머지 에러)

-makeSignature( ) 함수
secretKey를 기반으로 HmacSHA256 알고리즘을 통해 인코딩 하여 보안성을 확보


-SendLMS() , SENDSMS()
메시지 type 별로 2가지 전송 형태 (SMS, LMS)  80 바이트 기준으로 LMS type으로 문자 발송
