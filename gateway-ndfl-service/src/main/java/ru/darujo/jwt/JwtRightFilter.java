package ru.darujo.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public abstract class JwtRightFilter extends AbstractGatewayFilterFactory<JwtRightFilter.Config> {
    protected abstract List<String> getRight();

    protected JwtUtil jwtUtil;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtRightFilter() {
        super(Config.class);
    }


    @Override
    public @NonNull GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!isAuthMissing(request)) {
                final String token = getAuthHeaders(request);
                if (jwtUtil.isInvalid(token)) {
                    return this.onError(exchange, "Токен просрочен.", HttpStatus.UNAUTHORIZED);
                }
                try {
                    exchange = populateRequestHeader(exchange, token);
                } catch (RuntimeException ex) {
                    return this.onError(exchange, "gateWay: " + ex.getMessage(), HttpStatus.FORBIDDEN);
                }
            } else {
                return this.onError(exchange, "Токен отсутствует", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }


    private ServerWebExchange populateRequestHeader(ServerWebExchange exchange, String token) {

        Claims claims = jwtUtil.getAllClamsForToken(token);
        ArrayList<String> authorities = claims.get("authorities", ArrayList.class);
        if (getRight() != null) {
            if (authorities == null || authorities.stream().noneMatch(s -> getRight().contains(s))) {
                throw new RuntimeException("У вас недостаточно прав (" + getRight() + ")");
            }
        }

        ServerHttpRequest.Builder builder =
                exchange.getRequest().mutate()
                        .header("username", claims.getSubject());

        if (authorities != null) {
            authorities.forEach(s -> builder.header(s, "true"));
        }
        ServerHttpRequest serverHttpRequest = new ServerHttpRequestImpl(builder.build());
        MultiValueMap<String, String> params = serverHttpRequest.getQueryParams();
        try {
            params.add("system_project", Long.toString(claims.get("project", Long.class)));
        } catch (RuntimeException ignored) {

        }
        try {
            params.add("system_project_code", claims.get("project_code", String.class));
        } catch (RuntimeException ignored) {

        }
        if (authorities != null) {
            authorities.forEach(s -> params.add("system_right", s));
        }

        return exchange.mutate().request(serverHttpRequest).build();
    }

    private Mono<@NonNull Void> onError(ServerWebExchange exchange, String text, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        DataBufferFactory bufferFactory = response.bufferFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        DataBuffer wrap;
        try {
            wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(text));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DataBuffer finalWrap = wrap;
        return response.writeWith(Mono.fromSupplier(() -> finalWrap));
    }

    private String getAuthHeaders(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0).substring(7);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
            return true;
        }
        return !request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0).startsWith("Bearer");
    }
}
