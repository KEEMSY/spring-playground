# Spring Playground

| module name         | description                                                      |
|:--------------------|------------------------------------------------------------------|
| Spring-Data         | spring-boot-starter-data와 관련된 라이브러리를 종합한 프로젝트                    |
 | Spring-User         | Spring-Data 프로젝트를 활용한 사용자 관리 프로젝트                                |
| Spring-skeleton     | Spring-boot 사용 간, 필요한 기본 설정과 사용 예시를 확인하는데 사용되는 프로젝트              |
| Spring-kotlin-sync  | kotlin-Spring 사용 간, 필요한 기본 설정(Sync 설정)과 사용 예시를 확인하는데 사용되는 프로젝트   |
| Spring-kotlin-async | kotlin-Spring 사용 간, 필요한 기본 설정(Async 설정)과 사용 예시를 확인하는데 사용되는 프로젝트  |

## 공통 작성 규칙

| rule name           | description                                                                    |
|:--------------------|--------------------------------------------------------------------------------|
| Java Version        | Java JDK 17                                                                    |
| SpringBoot Version  | SpringBoot 3.X                                                                 |
| Unit testing        | Junit5, Mockito 를 활용한 단위 테스트 작성한다.                                             |
| Integration testing | 외부 컴포넌트 사용 시, dockerfile(혹은 docker-compose), TestContainers 를 활용한 통합 테스트 작성한다. |
| Controller 작성       | 실제 구동 및 테스트 진행을 위한 APIController 기본 구현, 기본 데이터 형식(JSON) 작성한다.                  |
| Logging             | 개발 간 로깅 작성한다.                                                                  |

--- 

## Spring-Data

- JPA 를 활용한, 엔터티 설계 및 사용방법을 연습하기 위한 프로젝트
- JPA, QueryDSL, JDBC, MongoDB, Redis 등 다양한 데이터베이스 연동 연습

---

## Spring-User

- JWT, Spring-Security 를 활용한 사용자 관리 프로젝트
- OAuth를 활용한 다양한 인증 방식 연습
  - KaKao 간편로그인 연동

---

## Spring-skeleton

- SpringBoot 사용 간, 필요한 기본 설정과 사용 예시를 확인하는데 사용되는 프로젝트

---

## Spring-kotlin-sync

- SpringBoot-Kotlin 사용 간, 필요한 기본 설정(Sync 설정)과 사용 예시를 확인하는데 사용되는 프로젝트
- MVC 아키텍처 및 최소한의 Port And Adapter 패턴을 적용하여, 의존서으이 방향이 Business 를 향하도록 아키텍처 설계

---

## Spring-kotlin-async

- SpringBoot-Kotlin 사용 간, 필요한 기본 설정(Sync 및 Async)과 사용 예시를 확인하는데 사용되는 프로젝트
- Skeleton 으로 활용할 수 있도록 기초 틀 구성
- MVC 아키텍처 및 최소한의 Port And Adapter 패턴을 적용하여, 의존서으이 방향이 Business 를 향하도록 아키텍처 설계