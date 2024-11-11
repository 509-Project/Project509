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

    // 유저 정보 (이메일, 비밀번호)를 CSV에서 가져오기
    FeederBuilder<String> userFeeder = csv("mockData3.csv").circular();

    // 시나리오 설정 (로그인, SSE 연결, 파티 생성 및 알림 확인을 하나로 묶음)
    ScenarioBuilder scenario = scenario("Notification Service Simulation")
            .feed(userFeeder)  // 각 사용자에게 고유의 이메일, 비밀번호 제공

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
//                                    "userRole": "#{userRole}"
//                                }
//                            """))
//                    .asJson()
//                    .check(status().is(200))
//            )


            // 로그인 시나리오
            .exec(http("Login Request")
                    .post("/auth/signin")
                    .header("Content-Type", "application/json")
                    .body(StringBody("""
                                {
                                    "email": "${email}",
                                    "password": "${password}"
                                }
                            """))
                    .asJson()
                    .check(header("Authorization").saveAs("jwtToken"))  // JWT 토큰 추출하여 저장
                    .check(status().is(200)) // 로그인 성공 확인
            )
            .exec(sse("SSE Connect")
                    .sseName("SSE Connect Stream")  // 스트림 이름을 설정
                    .connect("/notifications/connect")
                    .header("Authorization", "#{jwtToken}") // JWT 토큰 전달
                    .header("Content-Type", "text/event-stream")
                    .await(30) // 최대 30초 대기
                    .on(sse.checkMessage("SSE Check Notification")
                            .check(regex("\\{.*\\}").exists()) // 이벤트 데이터 존재 여부 확인
                    )
            )

            // 파티 생성 시나리오
            .exec(http("Create Party Request")
                    .post("/parties")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "#{jwtToken}")
                    .body(StringBody("""
                                {
                                    "marketName": "마켓 이름",
                                    "marketAddress": "마켓 주소",
                                    "itemId": 2,
                                    "itemCount": 3,
                                    "itemUnit": "2kg",
                                    "startTime": "2024-10-28T00:00:00",
                                    "endTime": "2024-10-28T10:00:00",
                                    "membersCount": 3
                                }
                            """))
                    .check(status().is(201)) // 파티 생성 성공 확인
            )
            // 알림 확인 시나리오
            .exec(sse("SSE Check Notification")
                    .sseName("Check Notification Stream")  // 스트림 이름 설정
                    .connect("/notifications/connect")
                    .header("Authorization", "#{jwtToken}") // JWT 토큰 전달
                    .header("Content-Type", "text/event-stream")
                    .await(30) // 최대 30초 대기
                    .on(
                            sse.checkMessage("Check Notification")
                                    .check(
                                            regex("\\{.*\\}").exists()  // 이벤트 데이터가 존재하는지 확인
                                    )
                    )
            );


    {
        // 여러 시나리오를 하나의 setUp에서 실행
        setUp(
                scenario.injectOpen(atOnceUsers(30))  // 모든 시나리오를 한 번에 실행
        ).protocols(httpProtocol);  // HTTP 프로토콜 설정
    }
}

