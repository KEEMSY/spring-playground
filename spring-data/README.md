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
  Page<Item> findPageByPrice(int price, Pageable pageable);
}
```

- Page 는 Slice 와 다르게 count 쿼리를 실행하여, 전체 데이터 개수와 전체 페이지 개수를 계산할 수 있다.
- 게시판의 페이지네이션 UI 등을 구현할 때 적합하다.

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