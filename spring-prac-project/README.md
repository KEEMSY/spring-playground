# Spring-Prac_Project

*공부한 spring 모듈에 대해서 종합하여 정리하는 프로젝트*

## 개요

프로젝트 세팅 간 반드시 필요한 항목들이다. 공부하고 현업 과정을 통해 해당 내용은 바뀔 수도 있다.

> **Architecture Level**

- Concurrency and Parallelism
- Scalability & Maintainability
- Backup & Recovery
- Monitoring & Logging
- Error Handling
- Testing
  - Unit Test
  - Integration Test

> **Development Practices**

- CI & CD
- Version Control
- Branch Strategy
- Deployment Strategy
- Code Review

> **API and Documentation**

- API Design
- Documentation
  - Change Log
  - Swagger

> Database and Data Management

- Database
- ORM (Object-Relational Mapping)
  - JPA (Java Persistence API)
  - QueryDSL
- SQL
- NoSQL
- Non-Blocking DB
- Caching
- Data Migration Strategy
- Backup & Recovery

> **Security**

- OAuth
- User Authentication and Authorization
- Data Encryption

<br><hr>

## Container 관리

### Ubuntu(Linux)

```shell
docker build -t ubuntu-container .
docker run -d -p LOCAL_PORT:CONTAINER_PORT --privileged=true --name ubuntu-container ubuntu-container /usr/sbin/init

# root 로 접근
docker exec -it -u 0 CONTAINER_NAME bash
```

- 이미지를 통한 ubuntu(Linux) 컨테이너 설정을 진행

> 패키지 관련

```shell
apt-get update && apt-get -y upgrade && apt-get install -y build-essential locales tzdata vim wget git curl cron rsync net-tools openssh-server sudo
```

- 패키지를 설치하기전, update, upgrade 를 진행한다.
- 필수요소 및 필요한 패키지를 설치한다.

*필요할 경우 계속하여 업데이트*

<br>

### Jenkins

```shell
# jenkins
# dockerfile 참조: https://github.com/wardviaene/jenkins-docker
# 인스턴스 내  docker 및 jenkins 설치 + 인스턴스 내에서 컨테이너 내에서 인스턴스(부모)에 설치된 도커에 접근을 가능하게 함
docker build -t jenkins-docker .
docker run -p INSTANCE_PORT:CONTAINER_PORT -p INSTANCE_PORT:CONTAINER_PORT -v /var/jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -d --name jenkins jenkins-docker
```

- 젠킨스는 개별 인스턴스를 띄워야한다.
- 젠킨스 인스턴스 내, 도커 컨테이너를 띄워 젠킨스 컨테이너에서 젠킨스 인스턴스(부모)의 docker 와 소켓 통신을 가능하게 한다.

> 에러 해결

```shell
jenkins@71b25fd71fca:/$ docker ps
Got permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock: Get http://%2Fvar%2Frun%2Fdocker.sock/v1.37/containers/json: dial unix /var/run/docker.sock: connect: permission denied

// 그룹이 추가되어있는지 확인. 안되있을 경우 그룹 추가
sudo chgrp docker /var/run/docker.sock // 그룹 추가
```