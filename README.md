# Security
## 1.로그인 
---
엑세스 토큰: 로그인 성공 시 엑세스 토큰을 로컬 스토리지에 저장한다

리프레쉬 토큰: 리프레쉬 토큰은 쿠키에 저장한다.


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

    

    

    

그런데 Token은 Cookie 안에서 저장되어야 하니까 Cookie의 유효기간이 Token의 유효기간보다 짧으면 로그아웃할때 

refreshtoken이 사라지지 않으니까 둘의 유효기간을 일치시키는 것이 이상적이다. 



그리고 로그인은 postmapping으로 진행하였는데 view controller에 hasRole을 추가 할 시, html 파일을 보여주기도 전에서버에서 무슨 역할을 가지고 있는지 확인을 해서 html을 아예 안보이는 문제가 생길 수 있기 때문에 api controller에 이를 추가한다 


![Screenshot 2024-11-15 170616](https://github.com/user-attachments/assets/5230c9a1-2a13-4a78-bc09-3294009f1263)



## 2. JWT 토큰 기반의 인증
---

1. 토큰 생성 및 검증
   
   TokenProvider 클래스에서 토큰 생성, 검증, 정보 추출을 담당
   토큰 생성: 사용자 정보와 만료기간을 설정해 JWT 발급
   토큰 검증: 유효한 토큰인지 검증하고 기간이 만료된 토큰이면 2, 유효하지 않은 경우 3을 반환한다.

public int validateToken(String token) {

        try {
        
            Jwts.parserBuilder()
            
                    .setSigningKey(getSecretKey())
                    
                    .build()
                    
                    .parseClaimsJws(token)
                    
                    .getBody();
                    
            log.info("Token validated");
            
            return 1;
            
        } catch (ExpiredJwtException e) {
        
            // 토큰이 만료된 경우
          
            log.info("Token is expired");
            
            return 2;
            
        } catch (Exception e) {
        
            // 복호화 과정에서 에러 발생
            
            log.info("Token is not valid");
            
            return 3;
            
        }
        
    }
 }   


2. controller
   
   WebSecurityConfig에 @EnableMethodSecurity(prePostEnabled = true)를 설정한 후

    ApiController에서 @PreAuthorize 어노테이션을 사용하여 ROLE_ADMIN 또는 글 작성자 본인만 상세 페이지를 볼 수 있도록 설정



@Slf4j

@Configuration

@RequiredArgsConstructor

@EnableMethodSecurity(prePostEnabled = true)

public class WebSecurityConfig {} 


@PreAuthorize("hasRole('ROLE_USER')")

  @GetMapping("/user")
  
  public void userpage() {
  
      System.out.println("user page");
      
  }


3. WebSecurityConfig

AccessDeniedHandler(403)와 AuthenticationEntryPoint(401) JSON 메시지로 변환해서 받아와 권한별로 페이지 이동 처리

      @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"You do not have permission to access this resource.\"}");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication is required to access this resource.\"}");
        };
    }


## 3. 인증 흐름
---

엑세스 토큰이 있을 경우: 프론트엔드에서 엑세스토큰이 로컬 스토리지에 존재하면 서버에 요청을 보낸다.

서버에서 토큰을 확인하고 유효성 검사를 해서 토큰이 만료되면 401에러를 보낸다

만약, 엑세스 토큰이 만료되었는데 refressh토큰이 남아있으면 access 토큰을 재발급하고 없으면 로그인 페이지로 리다이렉트 시킨다.


## 4. ROLE_USER, ROLE_ADMIN
----

일반 사용자는 user로 가입되게 하고 관리자는 admin으로 가입되도록 DB에서 role을 바꿔 관리한다
일반 사용자는 hasRole이 user인 페이지만 접근 가능하며 관리자 페이지는 못들어간다. 만약 일반 사용자가 관리자 페이지를 들어가려고 시도할 시 /access-denied 페이지가 나오도록 구현했다 

![Screenshot 2024-11-15 170412](https://github.com/user-attachments/assets/1cd8dda8-21eb-418e-9278-edf682a5e26a)


![Screenshot 2024-11-15 170759](https://github.com/user-attachments/assets/35dfecf0-8bb3-4fc0-9868-de5caee75313)


이 때, ajax가 response값을 html 구조로 받아버려 error 코드를 인지하지 못하여 hasRole이 무용지물되어버리는 문제가 발생했는데, security파일에서 ajax로 보내는 데이터를 error코드가 전송되게끔 수정함으로써 js ajax에서 받은 에러코드값에 따른 처리 코드를 구현함으로써 문제를 해결했다

 checkToken().then(() => {

        $.ajax({
            url:'/user',
            type:'GET',
            success: function (response) {
                console.log('res :: ', response)
            },
            error: function (xhr) {
                if(xhr.status === 401){
                    handleTokenExpiration();
                }else if (xhr.status === 403) {
                    window.location.href='/access-denied';
                }else{
                    alert("Unexpected error")
                }
            }
        });
    });

    
