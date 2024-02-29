# Spring Playground

| module name     | description                                         |
|:----------------|-----------------------------------------------------|
| Spring-Data     | spring-boot-starter-data와 관련된 라이브러리를 종합한 프로젝트       |
| Spring-skeleton | Spring-boot 사용 간, 필요한 기본 설정과 사용 예시를 확인하는데 사용되는 프로젝트 |

## 공통 작성 규칙

| rule name           | description                                                                    |
|:--------------------|--------------------------------------------------------------------------------|
| Java Version        | Java JDK 17                                                                    |
| Unit testing        | Junit5, Mockito 를 활용한 단위 테스트 작성한다.                                             |
| Integration testing | 외부 컴포넌트 사용 시, dockerfile(혹은 docker-compose), TestContainers 를 활용한 통합 테스트 작성한다. |
| Controller 작성       | 실제 구동 및 테스트 진행을 위한 APIController 기본 구현, 기본 데이터 형식(JSON) 작성한다.                  |
| Logging             | 개발 간 로깅 작성한다.                                                                  |

--- 

## Spring-Data

- JPA 를 활용한, 엔터티 설계 및 사용방법을 연습하기 위한 프로젝트
- JPA, QueryDSL, JDBC, MongoDB, Redis 등 다양한 데이터베이스 연동 연습

---

## Spring-skeleton

- Spring-boot 사용 간, 필요한 기본 설정과 사용 예시를 확인하는데 사용되는 프로젝트
