package ru.daru_jo.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.daru_jo.dto.ValCurs;

import java.util.Date;
import java.text.SimpleDateFormat;


@Slf4j
@Component
@ConditionalOnMissingClass
public class CbrServiceIntegration  extends ServiceIntegration {
    @Autowired
    public void setWebClient(WebClient webClientPay) {
        super.setWebClient(webClientPay);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public ValCurs userVacationStart(Date date) {


        try {
            return webClient.get().uri("?date_req=" + sdf.format(date))
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



}
