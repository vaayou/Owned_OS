package com.owned.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.web.servlet.function.RequestPredicates.path;

/**
 * Programmatic route definitions for Spring Cloud Gateway MVC (servlet stack).
 *
 * Flow: incoming /api/{prefix}/... → strip /api → forward to backend
 *   /api/auth/login      → user-service /auth/login
 *   /api/products/123    → commerce-service /products/123
 *   /api/collection/mine → collection-service /collection/mine
 *
 * Configured with configurable target URIs supporting Eureka load balancing (`lb://`)
 * or direct HTTP fallbacks (`http://localhost:port`).
 */
@Configuration
public class RouteConfig {

    @Value("${services.user-service.uri:lb://user-service}")
    private String userServiceUri;

    @Value("${services.commerce-service.uri:lb://commerce-service}")
    private String commerceServiceUri;

    @Value("${services.collection-service.uri:lb://collection-service}")
    private String collectionServiceUri;

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return buildRoute("user-service",
                path("/api/users/**").or(path("/api/auth/**")),
                userServiceUri);
    }

    @Bean
    public RouterFunction<ServerResponse> commerceServiceRoute() {
        return buildRoute("commerce-service",
                path("/api/products/**")
                        .or(path("/api/categories/**"))
                        .or(path("/api/wallet/**"))
                        .or(path("/api/orders/**"))
                        .or(path("/api/payments/**")),
                commerceServiceUri);
    }

    @Bean
    public RouterFunction<ServerResponse> collectionServiceRoute() {
        return buildRoute("collection-service",
                path("/api/collection/**").or(path("/api/certificates/**")),
                collectionServiceUri);
    }

    private RouterFunction<ServerResponse> buildRoute(String routeId, RequestPredicate predicate, String targetUri) {
        if (targetUri.startsWith("lb://")) {
            String serviceId = targetUri.substring(5);
            return route(routeId)
                    .route(predicate, HandlerFunctions.http())
                    .filter(FilterFunctions.stripPrefix(1))
                    .filter(LoadBalancerFilterFunctions.lb(serviceId))
                    .build();
        } else if (!targetUri.startsWith("http://") && !targetUri.startsWith("https://")) {
            return route(routeId)
                    .route(predicate, HandlerFunctions.http())
                    .filter(FilterFunctions.stripPrefix(1))
                    .filter(LoadBalancerFilterFunctions.lb(targetUri))
                    .build();
        } else {
            return route(routeId)
                    .route(predicate, HandlerFunctions.http(targetUri))
                    .filter(FilterFunctions.stripPrefix(1))
                    .build();
        }
    }
}

