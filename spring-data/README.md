# Spring-Data

- Spring Data JPA
- Spring Data MongoDB
- Spring Data Redis
- Spring Data R2DBC
- Spring Data Elasticsearch

<br><hr>

## Spring Data JPA

- 환경 설정
- 특징 
- 기능

  - 어노테이션  
  - Paging, Iterating Large Results, Sorting & Limiting

- 영속성 계층에 대한 유연성을 위한 설계 방법

<br>

> 환경 설정

**application.yml**
```yaml
# 현 기준은 MySQL
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/DBNAME?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8 # 본인의 db connection url
    username: name # 본인의 db username
    password: pw # 본인의 db password
    driver-class-name: com.mysql.cj.jdbc.Driver

    jpa:
      properties:
        hibernate.format_sql: true
      
      hibernate:
        ddl-auto: update # 데이터베이스 초기화 전략: none, create, create-drop, update, validate 가 존재
      
      show_sql: true        
      database-platform: org.hibernate.dialect.MySQL57Dialect
      open-in-view: false
```
- url 설정
  
    - DBNAME: 사용할 데이터베이스 이름
    - DBNAME?: 이후 설정할 프로퍼티 값 설정 

<br>

**build.gradle**

```
dependencies {
    implementation "org.springframework.boot:spring-boot-starter-data-jpa"
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    // === QueryDsl 시작 ===

    // ⭐ Spring boot 3.x이상
    implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
    annotationProcessor "com.querydsl:querydsl-apt:5.0.0:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"

    // === QueryDsl 끝 ===

    implementation "mysql:mysql-connector-java:${mysqlConnectorVersion}"
}

// === ⭐ QueryDsl 빌드 옵션 (선택) ===
def querydslDir = "$buildDir/generated/querydsl"

sourceSets {
    main.java.srcDirs += [ querydslDir ]
}

tasks.withType(JavaCompile) {
    options.annotationProcessorGeneratedSourcesDirectory = file(querydslDir)
}

clean.doLast {
    file(querydslDir).deleteDir()
}
// === ⭐ QueryDsl 빌드 옵션 (선택) 끝 ===
```

<br>

> 특징

- 애노테이션을 이용한 매핑
- String, int, LocalDate 등 기본적인 타입에 대한 매핑
- 커스텀 타입 변환기 적용(Money 타입 생성 및 칼럼에 매핑)
- 밸류 타입 매핑(한 개 이상 칼럼을 한 개 타입으로 매핑)
- 클래스 간 연관 지원(1-1, 1-N, N-1, N-M)
- 상속에 대한 매핑

<br>

> Pagination

```java
int page = 0;
int size = 10;
PageRequest page = PageRequest.of(pageNumber, offset);

// 정렬
PageRequest page = PageRequest.of(page, size, Sort.by("SOME PROPERTY").descending());
PageRequest page = PageRequest.of(page, size, Sort.by(Direction.DESC, "SOME PROPERTY"));
PageRequest page = PageRequest.of(page, size, Sort.by(Order.desc("SOME PROPERTY")));
PageRequest page = PageRequest.of(page, size, Direction.DESC, "SOME PROPERTY");
```

- `Pageable`, `PageRequest` 는 Spring Data 에서 제공하는 페이지네이션 정보를 담기위한 인터페이스 및 구현체이며, 페이지 번호와 단일 페이지의 개수를 담을 수 있다.
- 정렬은 `PageRequest.of()` 의 세번째 인자로 `Sort` 혹은 `Direction` 를 추가하면 된다.

<br>

```java
// 공통 부분
@ToString
@NoArgsConstructor
@Getter
@Entity
public class Item {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private int price;

  public Item(final String name, final int price) {
    this.name = name;
    this.price = price;
  }
}
```
```java
// Slice
public interface ItemRepository extends JpaRepository<Item, Long> { 
  // 메서드의 반환 타입을 Slice 로 지정하고, 파라미터로 Pageable 을 받는다.
  Slice<Item> findSliceByPrice(int price, Pageable pageable);
}
```

- Spring Data JPA 레포지토리에 Pageable 을 전달하면, 반환 타입으로 Slice 혹은 Page 를 받을 수 있다. 두 인터페이스 모두 페이지네이션을 통한 조회 결과를 저장하는 역할을 한다. 또한 Page 는 Slice 를 상속받는다.
- 전체 페이지 개수를 알아내기 위해서는 전체 데이터 개수 / 단일 페이지의 크기 로 계산해야한다.
- 전체 데이터 개수를 알아내기 위해서는 count 쿼리를 실행해야한다.
- Slice 는 별도로 count 쿼리를 실행하지 않는다. 따라서 전체 페이지의 개수와 전체 엔티티의 개수를 알 수 없지만, 불필요한 count 쿼리로 인한 성능 낭비는 발생하지 않는다.

<br>

```java
// Page
public interface ItemRepository extends JpaRepository<Item, Long> {
  // 메서드의 반환 타입을 Page 로 지정하고, 파라미터로 Pageable 을 받는다.
  Page<Item> findPageByPrice(int price, Pageable pageable);
}
```

- Page 는 Slice 와 다르게 count 쿼리를 실행하여, 전체 데이터 개수와 전체 페이지 개수를 계산할 수 있다.
- 게시판의 페이지네이션 UI 등을 구현할 때 적합하다.

<br>

> Spring Data MongoDB

**application.yml**
```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      authentication-database: admin
      database: MY-DATABASES
      username: sykeem
      password: 1234
```

<br>

**build.gradle**

```
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
implementation 'org.springframework.boot:spring-boot-starter-web'
```

<br>

> Spring Data Redis

**application.yml**
```yaml
spring:
  data:
    redis:
      port: 6379
      host: localhost
```

<br>

**build.gradle**

```
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

<br><hr>

## Entity Mapping
> 요약 
> - [@Entity](#entity), [@Table](#table) : 객체와 테이블 매핑
> - [@Column : 필드와 컬럼 매핑]()
> - [@Id: 기본 키 매핑]()
> - [@ManyToOne, @JoinColumn: 연관관계 매핑]()

<br>

### @Entity

 - @Entity 가 붙은 클래스는 JPA가 관리하는 클래스이며, 엔티티라고 한다.
 - JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 필수이다.
 - 속성: 네임
 - JPA에서 사용할 엔티티 이름을 지정한다.
 - 기본값: 클래스 이름을 그대로 사용한다.
   - 같은 클래스 이름이 없으면 가급적 기본 값을 사용하도록 한다.
 - 주의할 점
  - 엔티티 클래스는 기본 생성자가 반드시 있어야 한다.(파라미터가 없는 `public` 또는 `protected` 생성자)
  - final 클래스, infinal 클래스, enum, interface, inner 클래스에 사용할 수 없다.
- 저장할 필드에 final을 사용하면 안된다.

```java
@Entity
public class Member {
  @Id
  private Long id;
  private String name;
  private int age;
  
  public Member() {
  }
}
```

<br>


```java
@Entity(name = "Member_Name")
public class Member {
  @Id
  private Long id;
  private String name;
  private int age;

  public Member() {
  }
}
```

<br>

### @Table
- 엔터티와 매핑할 테이블을 지정한다.
- 생략하면 매핑한 엔티티 이름을 테이블 이름으로 사용한다.
- name 속성을 사용해서 테이블 이름을 지정할 수 있다.
- catalog, schema 속성은 DDL을 자동 생성할 때 사용한다.
  - catalog는 데이터베이스 catalog 매핑 된다.
  - schema는 데이터베이스 schema 매핑 된다.
- uniqueConstraints(DDL) 속성은 유니크 제약 조건을 지정할 때 사용한다.

```java
@Entity
@Table(name = "Member", uniqueConstraints = {@UniqueConstraint(
  name = "NAME_AGE_UNIQUE",
  columnNames = {"NAME", "AGE"}
)})
public class Member {
  @Id
  private Long id;
  private String name;
  private int age;
  
  public Member() {
  }
}
```

<br>

> 참고 사항 - 데이터베이스 스키마 자동 생성 관련
>  - DDL을 애플리케이션 실행 시점에 자동 생성한다.
>    - DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA 실행 로직에는 영향을 주지 않는다.
>  - 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL을 생성한다.
>    - hibernate.dialect 속성에 지정한 데이터베이스 방언을 사용한다.
>    - 사용하는 데이터베이스에 따라 다른 SQL을 생성한다.
>  - DDL 생성 기능은 DDL을 자동 생성할 때만 사용하고, 운영 환경에서는 사용하지 않는것이 좋다.
>    - create: 기존 테이블을 삭제하고 새로 생성한다.
>    - create-drop: create와 같으나 종료 시점에 테이블 DROP
>    - update: 변경분만 반영(운영 DB에는 사용하면 안됨)
>    - validate: 엔티티와 테이블이 정상 매핑되었는지만 확인한다.
>    - none: 아무것도 하지 않는다.>    
>    - 환경에 따른 옵션 추천
>      - 로컬 환경 혹은 초기 개발 단계: create 또는 update
>      - 테스트 서버: update 또는 validate
>      - 스테이징과 운영 서버: validate 또는 none

<br>

##  매핑 어노테이션
 - `@Column`: 컬럼 매핑
 - `@Temporal`: 날짜 타입 매핑(이제 잘 쓰이지 않음)
   - 날짜 타입(java.util.Date, java.util.Calendar)을 매핑할 때 사용 
   - TemporalType.DATE: 날짜, 데이터베이스 date 타입과 매핑(ex. 2024-08-02)) 
   - TemporalType.TIME: 시간, 데이터베이스 time 타입과 매핑(ex. 14:30:00)
   - TemporalType.TIMESTAMP: 날짜와 시간, 데이터베이스 timestamp 타입과 매핑(ex. 2024-08-02 14:30:00)\
   - LocalDate, LocalDateTime을 사용할 때는 생략 가능()
 - `@Enumerated`: enum 타입 매핑
   - EnumType.STRING: enum 이름을 그대로 저장
   - EnumType.ORDINAL: enum 순서를 저장(사용하지 않음)
 - `@Lob`: BLOB, CLOB 매핑
   - 문자 타입이면 CLOB 매핑
   - 나머지는 BLOB 매핑
 - `@Transient`: 특정 필드를 컬럼에 매핑하지 않음
   - DB에 저장하지 않을 필드에 사용
   - 메모리에서만 사용하고 싶을 때 사용(임시로 값을 보관하고 싶을 때 사용)

### @Column
| 속성 | 설명                          | 기본값 |
|---|-----------------------------|---|
| name | 필드와 매핑할 테이블의 컬럼 이름          | 객체의 필드 이름 |
| insertable | 등록 가능 여부                    | true |
| updatable | 변경 가능 여부                    | true |
| nullable | null 값의 허용 여부               | true |
| unique | 유니크 제약 조건                   | false |
| columnDefinition | 데이터베이스 컬럼 정보를 직접 설정         | |
| length | 문자 길이 제약 조건(String타입에서만 사용) | 255 |
| precision, scale | BigDecimal 타입에서 사용          | |

```java
@Entity
public class Member {
  @Id
  private Long id;
  
  @Column(name = "name", nullable = false)
  private String username;
  
  private int age;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedDate;
  
  @Lob
  private String description;
  
  @Transient
  private int temp;
  
  public Member() {
  }
}
```
<br>

### 기본 키 매핑
- `@Id`: 엔티티의 기본 키를 매핑할 때 사용(직접 매핑)
- `@GeneratedValue`: 기본 키 생성 전략을 명시
  - IDENTITY: 데이터베이스에 위임(MySQL)
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용(Oracle)
    - @SequenceGenerator 필요 
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
  - AUTO: 자동 선택(기본값)

<br>

> IDENTITY 전략
> - 기본 키 생성을 데이터베이스에 위임
> - IDENTITY는 데이터베이스에 위임하기 때문에 데이터베이스에 저장해야 할 때까지 기본 키 값을 알 수 없다.
> - IDENTITY 전략은 데이터베이스에 INSERT SQL을 실행한 후에 ID 값을 알 수 있다.(em.persist() 호출 시점에 즉시 INSERT SQL 실행하고 ID 값을 알 수 있다.)
> - 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
> - JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL을 실행
>   - AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID 값을 알 수 있다. 


```java
@Entity
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String name;
  
  public Member() {
  }
}
```

<br>

> SEQUENCE 전략
> - 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트이다.(ex. 오라클 시퀀스)
> - 데이터베이스 시퀀스는 유일성이 보장되어야 하며, 동시에 여러 트랜잭션에서 값을 가져가더라도 값이 순서대로 생성되어야 한다.
> - 데이터베이스 시퀀스는 성능, 확장성, 동시성 측면에서 최적화되어 있다.
>   - 시퀀스는 성능을 최적화하기 위해 캐싱을 사용할 수 있다. 예를 들어, 오라클 시퀀스는 기본적으로 20개의 값을 캐싱한다. 이는 성능을 향상시키지만, 시스템 충돌 시 캐시된 값이 손실될 수 있습니다. 
>   - 여러 트랜잭션이 동시에 시퀀스 값을 요청하더라도, 데이터베이스는 이를 효율적으로 처리할 수 있습니다.
> - SEQUENCE 전략은 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
> - @SequenceGenerator를 사용해서 매핑한다.
> - 주로 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용한다.

| 속성 | 설명                                                                                   | 기본값                |
|---|--------------------------------------------------------------------------------------|--------------------|
| name | 식별자 생성기 이름                                                                           | 필수                 |
| sequenceName | 데이터베이스에 등록되어 있는 시퀀스 이름                                                               | hibernate_sequence |
| initialValue | DDL 생성 시에만 사용되며, 시퀀스 DDL을 생성할 때 처음 시작하는 수를 지정한다.                                     | 1                  |
| allocationSize | 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용되며, 데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정한다.) | 50(주의)             |
| catalog, schema | 데이터베이스 catalog, schema 이름                                                            |

```java
@Entity
@SequenceGenerator(
  name = "MEMBER_SEQ_GENERATOR",
  sequenceName = "MEMBER_SEQ", // 매핑할 데이터베이스 시퀀스 이름
  initialValue = 1, allocationSize = 1
)
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
  private Long id;
  
  private String name;
  
  public Member() {
  }
}
```

<br>

> TABLE 전략
> - 테이블 전략은 별도의 테이블을 사용하여 유일한 식별자를 생성한다. 
> - 이 테이블은 주로 sequence 또는 id와 같은 이름을 가지며, 다음에 사용할 ID 값을 저장한다. 
> - 테이블 전략은 데이터베이스 독립적입니다. 즉, 모든 데이터베이스에서 사용할 수 있다. 
> - 성능은 시퀀스 전략에 비해 다소 떨어질 수 있습니다. 특히, 동시성 문제가 발생할 수 있다.
> - 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략이라고 말할 수 있다.
> - 장점
>   - 데이터베이스에 의존하지 않고, JPA가 모든 데이터베이스에서 사용할 수 있다.(모든 데이터베이스에 적용할 수 있다., 데이터베이스에 독립적이다.)
>   - 데이터베이스 시퀀스를 지원하지 않는 데이터베이스에서도 사용할 수 있다.
> - 단점
>   - 성능이 느리다.(테이블을 매번 업데이트 해야하므로, 트랜잭션이 많을 경우 성능 저하가 발생할 수 있다.)
>   - 동시성 문제가 발생할 수 있다.

| 속성 | 설명                                                                                                                                                       | 기본값                |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|
| name | 식별자 생성기 이름(다른 엔터티와 구분하기 위해 사용)                                                                                                                           | 필수                 |
| table | 키 생성 테이블명                                                                                                                                                | hibernate_sequence |
| pkColumnName | 시퀀스 컬럼명(테이블에서 기본 키로 사용할 컬럼 이름)                                                                                                                           | sequence_name       |
| valueColumnName | 시퀀스 값 컬럼명(다음 ID 값을 저장할 컬럼 이름)                                                                                                                            | next_val            |
| pkColumnValue | 키로 사용할 값의 이름(생성기가 사용할 기본 키 값)                                                                                                                            | 엔티티 이름           |
| initialValue | 초기 값(마지막으로 생성된 값이 기준이 된다.)                                                                                                                               | 1                   |
| allocationSize | 시퀀스 한 번 호출에 증가하는 수(ID 값을 증가시키는 단위. 기본값은 50, 성능 최적화에 사용되며, 데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정한다. 1로 설정 시, 매번 새로운 ID값을 생성할 때마다 테이블을 업데이트 한다.) | 50(주의)             |
| catalog, schema | 데이터베이스 catalog, schema 이름                                                                                                                                |
| uniqueConstraints | 유니크 제약 조건                                                                                                                                                |

```sql
CREATE TABLE MY_SEQUENCES (
    sequence_name VARCHAR(255) NOT NULL,
    next_val BIGINT,
    PRIMARY KEY (sequence_name)
);

```

```java
import jakarta.persistence.*;

@Entity
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnName = "sequence_name",
        valueColumnName = "next_val",
        pkColumnValue = "MEMBER_SEQ",
        allocationSize = 1
)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    private String name;

    public Member() {
    }

    // getters and setters
}

```