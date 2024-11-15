# Security
##1.로그인 
---

일단 MemberMapper에서 signIn 메서드를 만들고 memberService에서
사용자에게 받아온 정보와 DB에서 받아온 정보를 비교한다


Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );      

        SecurityContextHolder.getContext().setAuthentication(authentication);

        

그리고 꺼내온 사용자 정보를 member로 반환한다.
 Member member = ((CustomUserDetails) authentication.getPrincipal()).getMember();

 

또한, accessToken과 refreshToken을 회원가입 시 한번에 발급한다 
        // Access Token
        String accessToken = tokenProvider.generateToken(member, Duration.ofHours(2)); 

        // Refresh Token
        String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(2));

        

그리고 SignInResponseDTO에 이 정보를 담는다.


 return SignInResponseDTO.builder()
                .isLoggedIn(true)
                .message("로그인 성공")
                .url("/member/main")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(member.getUserId())
                .userName(member.getUserName())
                .build();
    } 

    

그런데 Token은 Cookie 안에서 저장되어야 하니까 Cookie의 유효기간이 Token의 유효기간보다 짧으면 로그아웃할때 refreshtoken이 사라지지 않으니까 둘의 유효기간을 일치시키는 것이 이상적이다. 



그리고 회원가입은 postmapping으로 진행하였는데 view controller에 hasRole을 추가 할 시, html 파일을 보여주기도 전에 서버에서 무슨 역할을 가지고 있는지 확인을 해서 html을 아예 안보이는 문제가 생길 수 있기 때문에 api controller에 이를 추가한다 



##2. ROLE_USER, ROLE_ADMIN
----

일반 사용자는 user로 가입되게 하고 관리자는 admin으로 가입되도록 DB에서 role을 바꿔 관리한다
일반 사용자는 hasRole이 user인 페이지만 접근 가능하며 관리자 페이지는 못들어간다. 만약 일반 사용자가 관리자 페이지를 들어가려고 시도할 시 /access-denied 페이지가 나오도록 구현했다 



이 때, ajax가 response값을 html 구조로 받아버려 error 코드를 인지하지 못하여 hasRole이 무용지물되어버리는 문제가 발생했는데, security파일에서 ajax로 보내는 데이터를 error코드가 전송되게끔 수정함으로써 js ajax에서 받은 에러코드값에 따른 처리 코드를 구현함으로써 문제를 해결했다
