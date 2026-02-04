package ru.daru_jo.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.daru_jo.properties.PropertyConnectionInterface;


import java.util.concurrent.TimeUnit;

@Slf4j
public class WebClientConfig {
    public WebClient webClient(PropertyConnectionInterface propertyConnection) {
        return webClient(propertyConnection, null);
    }

    public WebClient webClient(PropertyConnectionInterface propertyConnection, SslContext sslContext) {
        log.info(propertyConnection.getUrl());
        HttpClient httpClient = HttpClient.create().secure().option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS, propertyConnection.getConnectionTimeOut()
        ).doOnConnected(connection -> {
            connection.addHandlerLast(new ReadTimeoutHandler(propertyConnection.getReadTimeOut(), TimeUnit.MILLISECONDS));
            connection.addHandlerLast(new WriteTimeoutHandler(propertyConnection.getWriteTimeOut(), TimeUnit.MILLISECONDS));
        });
        if (sslContext != null) {
            httpClient = httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        }
        return WebClient
                .builder()
                .baseUrl(propertyConnection.getUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
