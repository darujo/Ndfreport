package ru.daru_jo.integration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.daru_jo.dto.ValCurs;

import java.util.Date;
import java.text.SimpleDateFormat;


@Slf4j
@Component
@ConditionalOnMissingClass
public class CbrServiceIntegration {
    private WebClient webClientCbr;

    @Autowired
    public void setWebClientCbr(WebClient webClientCbr) {
        this.webClientCbr = webClientCbr;
    }

    public ValCurs userVacationStart(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        addTeg(stringBuilder,"nikName",nikName);
//        addTeg(stringBuilder,"day",day);

        try {
            return webClientCbr.get().uri("?date_req=" + sdf.format(date))
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                            cR -> getMessage(cR, "Что-то пошло не так не удалось получить отпуск за период httpStatus "))
                    .bodyToMono(ValCurs.class)

                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .block();
        } catch (RuntimeException ex) {
            throw new RuntimeException("ЦБ не доступен " + ex.getMessage());
        }
    }

    @Data
    protected static class ErrorResponse {
        private String message;
        private int errorCode;
    }


    private Mono  <?extends @NonNull Throwable> getMessage(ClientResponse clientResponse, String message) {
        return clientResponse
                .bodyToMono(ErrorResponse.class)
                .flatMap(error -> {
//                            log.error("{} {}", message, error.getMessage());
                            return Mono.error(new RuntimeException(message + " " + error.getMessage()));
                        }

                );
    }


}
