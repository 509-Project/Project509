package computerdatabase;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class NotificationServiceSimulation extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080") // 기본 URL 설정
            .acceptHeader("application/json") // SSE 요청을 위한 Accept Header 설정
            .userAgentHeader("Gatling");

    // 미리 발급된 JWT 토큰을 하드코딩
    String hardCodedJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken2 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken3 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken4 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken5 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken6 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken7 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken8 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.
    String hardCodedJwtToken9 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ1c2VyNEA1MDkuY29tIiwidXNlclJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3MzE4NTU1MjgsImlhdCI6MTczMTg1MTkyOH0.1Fs5kdnjM92MosRQk_lfFLg30Q6vQcAtrTgYdihyNEo"; // 여기에 실제 JWT 토큰을 입력하세요.

//    // 유저 정보 (이메일, 비밀번호)를 CSV에서 가져오기
//    FeederBuilder<String> userFeeder = csv("mockData.csv").circular();

    // 시나리오 설정 (로그인, SSE 연결, 파티 생성 및 알림 확인을 하나로 묶음)
    ScenarioBuilder scenario = scenario("Notification Service Simulation")
//            .feed(userFeeder)  // 각 사용자에게 고유의 이메일, 비밀번호 제공
//
//            // 회원가입 시나리오
//            .exec(http("Signup Request")
//                    .post("/auth/signup")
//                    .header("Content-Type", "application/json")
//                    .body(StringBody("""
//                                {
//                                    "email": "#{email}",
//                                    "password": "#{password}",
//                                    "nickname": "#{nickname}",
//                                    "address": "#{address}",
//                                    "latitude": "#{latitude}",
//                                    "longitude": "#{longitude}",
//                                    "userRole": "#{userRole}"
//                                }
//                            """))
//                    .asJson()
//                    .check(status().is(200))
//            );


//            // 로그인 시나리오
//            .exec(http("Login Request")
//                    .post("/auth/signin")
//                    .header("Content-Type", "application/json")
//                    .body(StringBody("""
//                                {
//                                    "email": "${email}",
//                                    "password": "${password}"
//                                }
//                            """))
//                    .asJson()
//                    .check(header("Authorization").saveAs("jwtToken"))  // JWT 토큰 추출하여 저장
//                    .check(status().is(200)) // 로그인 성공 확인
//            )


//            .exec(session -> {
//                // JWT 토큰이 세션에 저장되는지 확인 (디버그)
//                System.out.println("JWT Token for User: " + session.getString("jwtToken"));
//                return session;
//            })


            .exec(sse("SSE Connect")
                    .sseName("SSE Connect Stream")  // 스트림 이름을 설정
                    .connect("/notifications/connect")
                    .header("Authorization", "Bearer " + hardCodedJwtToken)
                    .header("Content-Type", "text/event-stream")
                    .await(30) // 최대 30초 대기
                    .on(sse.checkMessage("SSE Check Notification")
                            .check(regex("\\{.*\\}").exists()) // 이벤트 데이터 존재 여부 확인
                    )
            );

//            // 파티 생성 시나리오
//            .exec(http("Create Party Request")
//                    .post("/parties")
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Bearer " + hardCodedJwtToken)
//                    .body(StringBody("""
//                                {
//                                    "marketName" : "이마트 역삼점",
//                                    "marketAddress" : "서울 강남구",
//                                    "latitude": "37.5173319258532",
//                                    "longitude": "127.047377408384",
//                                    "itemId" : 1,
//                                    "itemCount" : 3,
//                                    "itemUnit" : "kg",
//                                    "startTime" : "11-22 16:20",
//                                    "endTime" : "11-22 18:00",
//                                    "membersCount" : 4
//                                }
//                            """))
//                    .check(status().is(201)) // 파티 생성 성공 확인
//            )
            // 알림 확인 시나리오
//            .exec(sse("SSE Check Notification")
//                    .sseName("Check Notification Stream")  // 스트림 이름 설정
//                    .connect("/notifications/connect")
//                    .header("Authorization", "Bearer " + hardCodedJwtToken)
//                    .header("Content-Type", "text/event-stream")
//                    .await(30) // 최대 30초 대기
//                    .on(
//                            sse.checkMessage("Check Notification")
//                                    .check(
//                                            regex("\\{.*\\}").exists()  // 이벤트 데이터가 존재하는지 확인
//                                    )
//                    )
//            );


//    atOnceUsers(10), // 즉시 실행
//    rampUsers(50).during(10), // 10초 동안 50명의 사용자를 점진적으로 추가
//    constantUsersPerSec(20).during(1.minute()), // 1분 동안 매초 20명의 사용자를 추가
//    heavisideUsers(100).during(20.seconds) // 20초 동안 선형적으로 100명의 사용자를 증가
    {
        // 여러 시나리오를 하나의 setUp에서 실행
        setUp(
                scenario.injectOpen(rampUsers(200).during(10))  // 모든 시나리오를 한 번에 실행
        ).protocols(httpProtocol);  // HTTP 프로토콜 설정
    }
}

