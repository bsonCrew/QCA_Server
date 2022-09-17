# QCA_Server
2022 공개 SW 개발자 대회 Server

## 개발환경

서버는 **스프링부트 프레임워크**를 이용함. 아래는 `start.spring.io`를 사용한 스프링 부트 프로젝트 세팅 화면임.

![image](https://user-images.githubusercontent.com/33208303/190844811-f191374f-0fb5-4e69-8f9d-3c4b3ac25729.png)

사용한 라이브러리는 다음과 같음.

- 개발자들의 편의를 제공하는 Lombok, Spring Boot DevTools
- Spring MVC를 이용한 RESTful 서비스 개발을 위한 Spring Web
- JPA 사용을 위한 Spring Data JPA
- 데이터베이스로 mysql을 사용하기 위한 MySQL Driver
- 구현 코드에 대한 신뢰성 보장과 테스트 코드 작성, API 문서화를 위해 Spring REST Docs

---

## 주요 기능
### 추천 웹사이트 제공
메인 페이지에서, 사용자에게 **"가장 최근에 진단한 웹사이트"** 에 대한 정보를 제공함.

### 웹사이트 진단 수행
![image](https://user-images.githubusercontent.com/33208303/190844999-a38c59ea-879c-4e0e-95b0-4450850c4c45.png)

1. 사용자가 진단을 원하는 웹사이트를 입력해 request
2. DB에서 해당 웹사이트에 대한 데이터를 조회한 후, 진단된 날짜를 확인
  - 최근 진단 날짜가 1달 이내라면, 조회된 데이터를 반환
  - 그렇지 않으면 3으로 이동
3. Lighthouse API, Validator API를 사용해 웹사이트를 진단
4. 진단 결과를 저장하고, 사용자에게 반환
