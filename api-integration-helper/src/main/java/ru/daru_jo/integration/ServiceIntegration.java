package ru.daru_jo.integration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.darujo.exceptions.ResourceNotFoundRunTime;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public abstract class ServiceIntegration {
    protected WebClient webClient;

    protected void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Void test() {
        if (webClient == null) {
            throw new RuntimeException("Сервис не поддерживает тест соединение");
        }
        try {
            return webClient.get().uri("/test")
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                            cR -> getMessage(cR, "Проверка не прошла "))
                    .bodyToMono(Void.class)
                    .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                    .block();
        } catch (RuntimeException ex) {
            throw new ResourceNotFoundRunTime("Проверка не прошла или обратитесь к администратору " + ex.getMessage());
        }
    }

    public void shutDown(String token) {
        if (webClient == null) {
            throw new RuntimeException("Сервис не поддерживает тест соединение");
        }
        try {
            webClient.post().uri("/shutdownContext")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                            cR -> getMessage(cR, "Проверка не прошла "))
                    .bodyToMono(Void.class)
                    .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                    .block();
        } catch (RuntimeException ex) {
            throw new ResourceNotFoundRunTime("Проверка не прошла или обратитесь к администратору " + ex.getMessage());
        }
    }

    @Data
    protected static class ErrorResponse {
        private String message;
        private int errorCode;
    }


    protected Mono<? extends @NonNull Throwable> getMessage(ClientResponse clientResponse, String message) {
        return clientResponse
                .bodyToMono(ErrorResponse.class)
                .flatMap(error -> {
//                            log.error("{} {}", message, error.getMessage());
                            return Mono.error(new ResourceNotFoundRunTime(message + " " + error.getMessage()));
                        }

                );
    }

    @SuppressWarnings("SameParameterValue")
    protected void addTeg(StringBuilder stringBuilder, String str, Enum<?> value) {
        addTeg(stringBuilder, str, value.toString());
    }

    protected void addTeg(StringBuilder stringBuilder, String str, Date value) {
        addTeg(stringBuilder, str, dateToText(value));
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

    protected void addTeg(StringBuilder stringBuilder, String str, Object value) {
        if (value != null) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("&");
            } else {
                stringBuilder.append("?");
            }
            stringBuilder.append(str).append("=").append(value);
        }
    }

    protected void addTeg(StringBuilder stringBuilder, String str, Iterable<?> valueList) {
        if(valueList != null) {
            valueList.forEach(value -> addTeg(stringBuilder, str, value));
        }
    }
    protected void addTeg(StringBuilder stringBuilder, String str, Long value) {
        if (value != null) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("&");
            } else {
                stringBuilder.append("?");
            }
            stringBuilder.append(str).append("=").append(value);
        }
    }

    protected void addTeg(StringBuilder stringBuilder, String str, Integer value) {
        if (value != null) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("&");
            } else {
                stringBuilder.append("?");
            }
            stringBuilder.append(str).append("=").append(value);
        }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String dateToText(Date date) {
        if (date == null) {
            return null;
        }
        return sdf.format(date) + "T00:00:00.000Z";
    }

}
