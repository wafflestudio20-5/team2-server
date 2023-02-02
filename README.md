

![ktlint check](https://github.com/wafflestudio20-5/team2-server/actions/workflows/ktlint_check.yml/badge.svg)
[![Deploy](https://github.com/wafflestudio20-5/team2-server/actions/workflows/deploy.yml/badge.svg)](https://github.com/wafflestudio20-5/team2-server/actions/workflows/deploy.yml)

<div align="center">
    <img src="https://user-images.githubusercontent.com/86216809/216102204-712654a2-13e1-46f5-89c3-0cd68ea590d7.jpg" width="150" height="150" />
</div>

# 지식2n
## 세상의 모든 질문에는 답변이 있다

앱 소개
![지식인소개3](https://user-images.githubusercontent.com/86216809/216120258-43bcc3c8-0067-4de2-b01f-56861b400243.jpg)
지식2n은 "세상의 모든 질문에는 답변이 있다"는 캐치프레이즈를 가진, 질문하고 답변하는 앱입니다.<br>
이 공간에서 여러분들은 어떠한 궁금한 거라도 질문할 수 있고, 내가 알고 있는 어떤 지식도 답변을 달 수 있습니다.<br>
저희 앱의 이름의 유래는 '세상에 질문이 n개 있다면, 그에 대한 답변도 n개 있지 않을까?','그렇다면 총 2n개의 글이 달리지 않을까?'에서 시작했습니다.<br>
Team2에서 만든 공간에서 즐겁게 뛰어놀아 주세요.<br>

누가 만들었나요?<br>
Spring developer: 우혁준, 이지원, 조성규<br>
iOS developer: 김령교, 박정헌, 박채현, 홍희주<br>

워크플로우&프로젝트 뷰<br>
핵심적인 기능들을 추린 워크플로우는 다음과 같습니다.<br>
![워크플로우1페이지2](https://user-images.githubusercontent.com/86216809/216283042-1a5b266e-5d3b-4941-b51d-87a39b9b4e1d.jpg)
![워크플로우2페이지](https://user-images.githubusercontent.com/86216809/216114406-4a345997-a87d-4155-8893-a808e502a5e6.jpg)
![워크플로우3페이지](https://user-images.githubusercontent.com/86216809/216114630-658381d7-23e4-47d4-944e-9e3a0cebeddb.jpg)


지식2n 사용 설명서

지식2n에는 다양한 기능이 있지만, 그중에서 가장 핵심적인 기능은 "질문하고, 답변해서, 반응하기"라고 할 수 있습니다.<br>
Team2는 이 과정을 어떻게 구현하였을까요?<br>
시연에서는 직접 두눈으로 보여드렸지만, 다른 팀꺼 본다고/직접 발표한다고/개인상의 일정 때문에 등등의 이유로 못 본 분들을 위해!<br>
지식2n 리드미에서 핵심 기능이 무엇인지 깔끔하게 설명해드리겠습니다 :blush:<br>


![사용설명서1페이지](https://user-images.githubusercontent.com/86216809/216321167-7e3a28c6-46fb-4d1c-a961-bd693f13a139.jpg)
![사용설명서2페이지](https://user-images.githubusercontent.com/86216809/216321186-a7a7fcd2-670a-4dae-83ad-20a309e47f4e.jpg)
![사용설명서3페이지](https://user-images.githubusercontent.com/86216809/216321205-f6921b59-d910-43c9-92fb-b2f3cb6ebb11.jpg)
![사용설명서4페이지](https://user-images.githubusercontent.com/86216809/216321218-4e7bab7c-62e4-4a16-9d3f-fbbfe6336906.jpg)


이외에도 다음과 같이 다양한 기능을 담았습니다.

![다양한기능1페이지](https://user-images.githubusercontent.com/86216809/216355237-d36ad792-eba0-47f8-b712-eaab34aeb8ea.jpg)
![다양한기능2페이지](https://user-images.githubusercontent.com/86216809/216355256-ae32178c-2881-4f6a-8240-3b9d62e64b13.jpg)
![다양한기능3페이지](https://user-images.githubusercontent.com/86216809/216355280-922da1b6-29a4-45b0-9fec-7cd5143635cb.jpg)
![다양한기능4페이지](https://user-images.githubusercontent.com/86216809/216355299-cf1234c9-eb32-4e55-896b-c1e71f2776eb.jpg)
![다양한기능5페이지](https://user-images.githubusercontent.com/86216809/216355327-c88b3d7b-e473-49de-b2ae-a2463390bde2.jpg)


우리, 이것도 구현했다!

지식2n을 만들면서 기능적으로 여러 난관을 겪었습니다.<br>
처음에 난관을 만났을 때는 우왕좌왕했지만, 구글링도 하고 다른 사람들과 지식을 공유하면서 난관들을 하나씩 극복해갔습니다.<br>
그러한 시련과 극복을 거친 끝에, 이렇게 멋있는 앱이 탄생할 수 있었습니다.

아래의 글부터는, 지식2n에서 자랑하고 싶은 내용을 하나씩 짚어가며 소개하고자 합니다.<br>
Team2 리드미의 하이라이트라고 볼 수 있을 것 같네요 :blush:<br>
그러면 지금부터 시작하겠습니다!<br>
(아래의 사진부터는 발표 자료로, 발표 자료를 보강하는 내용을 글로 적도록 하겠습니다.)


소셜 로그인(카카오)
   ![구현_카카오로그인](https://user-images.githubusercontent.com/86216809/216363896-10e9e3b3-b943-44f7-907f-ec0c4985df6a.jpg)
    와플 토이프로젝트를 만들며 필수적으로 해결하라고 한 요소 중 하나였던 카카오 로그인!<br>
    지식2n에서는 다양한 소셜 로그인 중 카카오 로그인을 도입하였습니다.<br>
    우선 Team2는 Kakao Developer에 통해 지식2n 앱을 등록하고, iOS를 기반으로 하고 있기 때문에 iOS 플랫폼을 도입하였습니다.<br>
    카카오 로그인에서 정말 중요하게 다뤄지는 것 중 하나가 토큰인데요, 토큰을 주고받는 순서를 간단히 잡으면<br>
    'iOS는 카카오 토큰을 받음->받은 카카오 토큰을 스프링에 전달->스프링은 다시 카카오한테 토큰을 줘서 유저 정보를 전달받음->전달받은 유저 정보를 스프링 db에 전달'<br>
    순서로 전개됩니다.<br>
    이 과정을 통해, 지식2n에서도 카카오 유저의 필요한 정보들을 받을 수 있고, 버튼 하나를 클릭하면서 간단하게 로그인을 실행시킬 수 있습니다.


정렬&페이지네이션
   ![구현_정렬페이지네이션](https://user-images.githubusercontent.com/86216809/216401869-81343a47-2a59-4f24-92b7-1b09afb1621d.jpg)
   답변하기, 나의질문 등 지식2n에서는 다양한 질문을 다루어야 하기 때문에, 기준에 따른 정렬과 페이지네이션이 중요합니다.<br>
   이를 구현하기 위해 QueryDsl을 적극적으로 사용하였으며, 자세히 살펴보면<br>
   orderBy와 desc를 통해 최신순, 좋아요순, 답변수순을 통해 질문들을 역방향으로 정렬하였으며(답변수순은 앱 미반영)<br>
   offset(page*amount), limit(amount)를 통해 각 페이지 첫번째 글부터 amount 번째까지의 글을 불러오도록 하였습니다.


Https
   ![구현_https1페이지](https://user-images.githubusercontent.com/86216809/216403204-103aa762-fe35-4f1d-aa7a-412348a5a131.jpg)
   ![구현_https2페이지](https://user-images.githubusercontent.com/86216809/216403249-5a4d6183-b3c2-4b79-9b8f-758b24df7719.jpg)
   Https(Hypertext Transefer Protocol Secure)는 Http에 SSL 보안을 얹은 것으로, Http에서 보안성과 사이트 로딩 속도가 뛰어납니다.<br>
   Https 구현을 설명하기 위해서는 먼저 AWS의 Elastic Beanstalk을 설명해야 합니다.<br>
   Elastic Beanstalk는 docker를 통한 EC2 배포, AWS 데이터베이스인 RDS를 하나로 묶어 구성할 뿐만 아니라<br>
   로드 밸런싱, 프로비저닝, 오토 스케일링 등 앱의 최적화를 위해 필요한 작업들을 자동으로 해줍니다.<br>
   여기에서 Https와 관련이 깊은 것은 로드 밸런싱으로, 로드 밸런싱은 트래픽이 많은 서버의 과부화를 방지하기 위해<br>
   트래픽을 여러 서버에 균등하게 분배하는 역할을 하고 있습니다.<br>
   Https가 로드 밸런싱과 연관이 깊은 이유는, 구매한 도메인을 Route 53에 등록하고 나서 Elastic Beanstalk의 로드 밸런서와 연결시켜야 하기 때문입니다.
   그 과정에서 Amazon Certificate Manager를 통해 인증서를 발급받아 두 연결을 https로 하였기 때문입니다.