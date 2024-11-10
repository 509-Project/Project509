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
    FeederBuilder<String> userFeeder = csv("users.csv").circular();

    // 시나리오 설정 (로그인, SSE 연결, 파티 생성 및 알림 확인을 하나로 묶음)
    ScenarioBuilder scenario = scenario("Notification Service Simulation")
            // 로그인 시나리오
            .feed(userFeeder)  // 각 사용자에게 고유의 이메일, 비밀번호 제공
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
                    .connect("/notifications/connect")
                    .header("Authorization", "#{jwtToken}") // JWT 토큰 전달
                    .header("Content-Type", "text/event-stream")
                    .await(30) // 최대 30초 대기
                    .on(sse.checkMessage("SSE Check Notification")
                            .check(regex("\\{.*\\}").exists()) // 이벤트 데이터 존재 여부 확인
                    )
            )

            // 알림 확인 시나리오
            .exec(sse("SSE Check Notification")
                    .connect("/notifications/connect")
                    .header("Authorization", "#{jwtToken}") // JWT 토큰 전달
                    .header("Content-Type", "text/event-stream")
                    .await(30) // 최대 30초 대기
                    .on(
                            sse.checkMessage("Check Notification")
                                    .check(
                                            jsonPath("$.id").exists(), // id가 존재하는지 확인
                                            jsonPath("$.content").is("참가 신청한 '미역' 품목의 파티가 생성되었습니다."), // 알림 내용 확인
                                            jsonPath("$.type").is("PARTY_CREATE"), // 알림 타입 확인
                                            jsonPath("$.url").is("http://localhost:8080/parties/1"), // URL 확인
                                            jsonPath("$.isRead").is("false"), // isRead 값이 false인지 확인
                                            jsonPath("$.createdAt").is("2024-11-09T01:12:23.711886") // 생성일시 확인
                                    )
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
            );

    {
        // 여러 시나리오를 하나의 setUp에서 실행
        setUp(
                scenario.injectOpen(atOnceUsers(4))  // 모든 시나리오를 한 번에 실행
        ).protocols(httpProtocol);  // HTTP 프로토콜 설정
    }
}

