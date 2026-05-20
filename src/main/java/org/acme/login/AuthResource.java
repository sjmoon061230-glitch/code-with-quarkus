package org.acme.login;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.io.InputStream;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.vertx.ext.web.RoutingContext;

@Path("/") 
public class AuthResource {

    @Inject
    RoutingContext context; // Quarkus Vert.x 세션 접근

    // GET /login → 로그인 HTML 페이지 반환
    @GET
    @Path("/login") 
    @Produces(MediaType.TEXT_HTML) 
    public Response loginPage() {
        InputStream html = getClass()
            .getClassLoader()
            .getResourceAsStream("META-INF/resources/login/login.html");
        return Response.ok(html).build();
    }

    // POST /login_check → 로그인 인증 처리
    @POST
    @Path("/login_check")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) 
    @Transactional
    public Response loginCheck(
            @FormParam("username") String username,
            @FormParam("password") String password) {

        User user = User.findByUsername(username); // 아이디 조회
        
        // 회원이 없거나 비밀번호가 틀린 경우
        if (user == null || !user.password.equals(password)) { 
            return Response
                .seeOther(URI.create("/login?error=1"))
                .build();
        }

        // 세션에 로그인 정보 저장
        context.session().put("loginUser", username);
        return Response
            .seeOther(URI.create("/after_login"))
            .build();
    }

    // GET /after_login → 로그인 후 메인 페이지
    @GET
    @Path("/after_login")
    @Produces(MediaType.TEXT_HTML)
    public Response afterLogin() {
        // 세션 체크: 로그인 안 한 사용자 차단
        String loginUser = context.session().get("loginUser");
        
        // 세션 내용 로그 출력
        System.out.println("=== 세션 ID : " + context.session().id());
        System.out.println("=== loginUser : " + loginUser);
        
        if (loginUser == null) {
            // 세션 없음 → 로그인 페이지로 강제 이동
            return Response
                .seeOther(URI.create("/login"))
                .build();
        }
        
        // 세션 있음 → 로그인 후 HTML 반환
        InputStream html = getClass()
            .getClassLoader()
            .getResourceAsStream("META-INF/resources/login/main_after_login.html");
        return Response.ok(html).build();
    } // <-- 기존에 클래스가 닫히던 중괄호를 메서드 종료로만 사용합니다.

    // GET /logout → 로그아웃 처리
    @GET
    @Path("/logout")
    public Response logout() {
        // 로그아웃 전 세션 정보 출력
        if (context.session() != null) {
            System.out.println("=== 로그아웃 전 세션 ID : " + context.session().id());
            System.out.println("=== 로그아웃 전 loginUser : " + context.session().get("loginUser"));
            
            // 세션 전체 파기
            context.session().destroy();
        }
        
        System.out.println("=== 로그아웃 완료 ===");
        
        // 메인 페이지 혹은 로그인 페이지로 리다이렉트
        return Response
            .seeOther(URI.create("/"))
            .build();
    }
} // <-- 클래스를 닫는 중괄호는 항상 파일의 최하단에 위치해야 합니다!