package com.owned.gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.web.servlet.function.RequestPredicates.path;

/**
 * Programmatic route definitions for Spring Cloud Gateway MVC (servlet stack).
 *
 * Flow: incoming /api/{prefix}/... → strip /api → forward to backend
 *   /api/auth/login      → user-service:8081/auth/login
 *   /api/products/123    → commerce-service:8082/products/123
 *   /api/collection/mine → collection-service:8083/collection/mine
 *
 * Note: In Gateway Server WebMVC 5.x, HandlerFunctions.http() is no-arg.
 * The target URI is set via BeforeFilterFunctions.uri() instead.
 */
@Configuration
public class RouteConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .route(
                    path("/api/users/**").or(path("/api/auth/**")),
                    HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                .filter(FilterFunctions.stripPrefix(1))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> commerceServiceRoute() {
        return route("commerce-service")
                .route(
                    path("/api/products/**")
                        .or(path("/api/wallet/**"))
                        .or(path("/api/orders/**"))
                        .or(path("/api/payments/**")),
                    HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri("http://localhost:8082"))
                .filter(FilterFunctions.stripPrefix(1))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> collectionServiceRoute() {
        return route("collection-service")
                .route(
                    path("/api/collection/**").or(path("/api/certificates/**")),
                    HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri("http://localhost:8083"))
                .filter(FilterFunctions.stripPrefix(1))
                .build();
    }
}
