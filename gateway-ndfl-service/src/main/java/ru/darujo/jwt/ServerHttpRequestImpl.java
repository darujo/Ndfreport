package ru.darujo.jwt;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

public class ServerHttpRequestImpl extends ServerHttpRequestDecorator {

    // your own query params implementation
    private final MultiValueMap<String, String> queryParams;

    public ServerHttpRequestImpl(ServerHttpRequest request) {
        super(request);
        this.queryParams = new LinkedMultiValueMap<>();
        request.getQueryParams().forEach((s, strings) -> {
            if (!s.startsWith("system_")) {
                this.queryParams.addAll(s, strings);
            }
        });


    }

    @Override
    @NullMarked
    public MultiValueMap<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    @NullMarked
    public URI getURI() {
        String url = getDelegate().getURI().toString();
        if (url.indexOf("?") > 0) {
            url = url.substring(0, url.indexOf("?"));
        }
        StringBuilder sb = new StringBuilder();
        queryParams.forEach((s, strings) -> strings.forEach(s2 -> addTeg(sb, s, s2)));

        return URI.create(url + sb.toString().replace(" ", "+"));


    }

    protected void addTeg(StringBuilder stringBuilder, String str, String value) {
        if (value != null && !value.isEmpty()) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("&");
            } else {
                stringBuilder.append("?");
            }
            stringBuilder.append(str).append("=").append(value);
        }
    }

    //override other methods if you want to modify the behavior
}
