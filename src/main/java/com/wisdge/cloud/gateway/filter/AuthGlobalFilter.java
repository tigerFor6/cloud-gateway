package com.wisdge.cloud.gateway.filter;

import com.nimbusds.jose.JWSObject;
import com.wisdge.cloud.gateway.internal.AuthConstant;
import com.wisdge.cloud.internal.CoreConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.text.ParseException;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 */
@Slf4j
@Component
@Order(0)
public class AuthGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            return chain.filter(exchange);
        }
        try {
            // 从token中解析用户信息并设置到Header中去
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String userStr = jwsObject.getPayload().toString();
            log.info("AuthGlobalFilter user:{}", userStr);
            ServerHttpRequest request = exchange.getRequest().mutate().header(CoreConstant.JWT_HEADER_USER_INFO, userStr).build();
            exchange = exchange.mutate().request(request).build();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return chain.filter(exchange);
    }
}
