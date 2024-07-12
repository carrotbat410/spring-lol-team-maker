# 롤 내전 도우미(Spring API Server)
![롤내전도우미](https://github.com/carrotbat410/spring-lol-team-maker/assets/163713062/64e846aa-a151-430b-97f4-47ad48a1485b)
![롤내전도우미](https://github.com/carrotbat410/spring-lol-team-maker/assets/163713062/8fb09026-c11f-4143-b5aa-d6c51d2b6e51)

> 롤 내전 도우미에서 밸런스 있는 내전 팀 결과를 생성해보세요!

- 롤 내전 게임시, 균형있는 게임을 위해 티어 차이가 적은 두 팀을 보여주는 서비스입니다.
- 라이엇 API를 사용하여 유저 정보를 불러올 수 있습니다.
- 커뮤니티 기능(연습경기 상대 모집 / 자유 게시판 / 내전 클랜 홍보)이 추가 될 예정입니다.
- [바로가기](https://lolcivilwarhelper.vercel.app/)

## 📆 프로젝트 기간
- 2024/04/26 ~ 2024/07/09

## 📚 기술 스택

| 기술                                  | 설명                                                                                                                |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `Java 17`                           |                                                                                                                   |
| `Spring Boot 3.2.5`                 | Spring 프레임워크를 기반으로 한 애플리케이션을 빠르게 구성하고 개발할 수 있게 해주는 프레임워크로, 간편한 설정과 다양한 내장 기능을 제공합니다.                              |
| `JPA`,`Spring Data JPA`,`QueryDsl`  | 객체지향적인 데이터베이스 접근을 제공하는 JPA와 이를 더 쉽게 사용할 수 있게 해주는 Spring Data JPA, 그리고 타입 안정성 있는 동적 쿼리를 작성할 수 있는 QueryDsl을 사용했습니다. |
| `Lombok`                            | 어노테이션 기반의 코드 자동 생성을 통해 생산성을 향상시켰습니다.                                                                              |
| `JUnit5`                            | 단위 테스트를 통해 코드의 신뢰성을 높이고 버그를 사전에 방지할 수 있습니다.                                                                       |
| `Mockito`                           | Mock 객체를 사용하여 외부 의존성을 제거한 단위 테스트를 작성하였습니다.                                                                        |
| `Spring Security`, `JWT`            | 추후 모바일 앱 개발을 고려해 토큰 방식의 인증 절차를 관리했습니다. 세션 상태를 서버에 저장할 필요 없이 클라이언트 측에서 토큰을 관리하기에 적은 서버 리소스 소모의 이점이 있습니다.           |
| `Prometheus`, `Grafana`, `Telegram` | EC2와 RDS의 CPU 사용률을 주요 지표로 설정하여 지속적으로 관찰했으며, 경보가 발생하면 Slack을 통해 즉각적인 알림을 받아 이슈에 빠르게 대응할 수 있었습니다.                   |
| `MySQL`                             | 관계형 데이터베이스 관리 시스템(RDBMS)으로, 데이터의 무결성과 성능을 보장하며 다양한 쿼리 기능을 제공합니다.                                                  |
| `Redis`                             | Redis를 도입하여 자주 사용되는 데이터를 캐싱함으로써 응답 속도를 개선하고 데이터베이스 부하를 줄였습니다.                                                     |
| `Github Actions`                    | 러닝 커브가 낮아서 새로운 사용자도 빠르게 파이프라인을 설정하고 자동화 과정을 시작할 수 있습니다. 이를 통해 개발부터 배포까지의 프로세스를 빠르고 안정적으로 진행할 수 있었습니다.             |
| `AWS EC2`                           | 클라우드 컴퓨팅 서비스를 이용하였습니다.                                                                                            |
| `Docker`, `Docker Compose`          | Docker Compose를 사용해 여러 컨테이너(App, Redis, MySQL)를 손쉽게 관리하였습니다.                                                      |
| `Nginx`                             | 리버스 프록시 서버 및 로드 밸런서로 사용하였습니다.                                                                                     |


## 🏗️ 서비스 아키텍처
![아키텍처](https://github.com/user-attachments/assets/8a363ff0-79ed-4b8e-930f-f358895f6f63)


## 🔖 ERD

![diagram](https://github.com/carrotbat410/spring-lol-team-maker/assets/163713062/a666dca1-d58b-47e4-ab57-cb0b48d1ab3f)

## 🔥 트러블 슈팅

<details>
<summary>대용량 트래픽 처리</summary>

`문제사항`
- 고사양의 맥북에서 띄운 로컬 서버에 대한 부하테스트임에도 불구하고, 평균 응답속도가 3600ms로, 대용량 트래픽에 대해 취약한 결과가 나왔습니다.
```text
# 테스트 조건
Number of Threads(유저 수): 1000
Ramp-Up Period(전체 thread가 전부 실행되는데까지 걸리는 시간): 30
Loop Count(각 Thread가 몇번씩 실행을 할 것인지): 2
한 유저가 요청하는 API: 5개
=> 초당 330개의 API가 실행됩니다.
```
![개선전](https://github.com/carrotbat410/spring-lol-team-maker/assets/163713062/f9b4ca9f-d474-413f-acb3-1e07adfd7dc9)

`해결방법`

### 1. OSIV(Open Session In View) OFF
- 영속성 컨텍스트가 뷰 계층까지 연장되지 않도록 하여, 짧은 트랜잭션 생명주기를 유지하도록 하였습니다.
### 2. 쿼리 최적화: 실행되는 쿼리를 확인후, 효율적인 쿼리로 개선하였습니다.
- 게시글 작성 API: getReferenceById를 사용하여 Insert 쿼리만 수행되도록 변경.
- 게시글 조회 API: 페이징을 위한 JPA의 기본 Count 쿼리를 Join 없는 효율적인 count쿼리로 변경.
- 내가 쓴 글 조회 API: 작성자는 본인이고, 이는 토큰에 저장되어있으므로, Spring Data JPA의 사용자 정의 쿼리를 사용하여 Join없는 쿼리로 변경.
### 3. Readonly Transactional
- 데이터를 읽기만 한다는 것을 DB에 알려줌으로써 동작 및 쿼리를 최적화할 수 있다.
### 4. DB Connection Fool 설정
- 커넥션 풀 크기를 기본값에서, 서버 스펙에 맞는 최적의 커넥션 풀 사이즈로 설정하였습니다.
### 5. Redis 사용
-  자주 사용되는 데이터를 캐싱함으로써 응답 속도를 개선하고 데이터베이스 부하를 줄였습니다.

`개선 결과`
- 평균 응답 속도: 3600ms -> 520ms으로 85.5%가 개선되었습니다.
- Std. Dev.(표준 편차): 2000ms -> 490ms으로 75.5%가 개선되었습니다.

![개선후](https://github.com/carrotbat410/spring-lol-team-maker/assets/163713062/35cd1f8e-bbb5-4593-9078-76da02e64123)

</details>


