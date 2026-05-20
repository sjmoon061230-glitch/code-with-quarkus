package org.acme.login;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.vertx.core.Vertx;

@ApplicationScoped // 1. Quarkus 관리 빈(Bean)으로 명시
public class SessionConfig {

    @Inject
    Vertx vertx;

    // 2. StartupEvent 또는 기존 Router 이벤트를 명확히 명시
    public void init(@Observes Router router) {
        router.route().handler(
            SessionHandler
                .create(LocalSessionStore.create(vertx))
                .setSessionTimeout(60 * 60 * 1000L) // 1시간
                .setCookieHttpOnlyFlag(true)
        );
        
        System.out.println("=== Vert.x 세션 핸들러 등록 완료 ===");
    }
}