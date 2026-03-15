package ru.daru_jo.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.exceptions.ResourceNotFoundException;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;

import java.util.List;

@Slf4j
@Component
@ConditionalOnMissingClass
public class PayServiceIntegration extends ServiceIntegration {
    @Autowired
    public void setWebClient(WebClient webClientPay) {
        super.setWebClient(webClientPay);
    }


    public List<PayDTO> getPayList(List<Long> orderId) {
        StringBuilder stringBuilder = new StringBuilder();
        addTeg(stringBuilder, "orderId", orderId);
        String uri = "/list" + stringBuilder;
        try {
            return webClient.get().uri(uri)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                            cR -> getMessage(cR, "Что-то пошло не так не удалось получить работы за период"))
                    .bodyToFlux(PayDTO.class).collectList()
                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .block();
        } catch (RuntimeException ex) {
            throw new ResourceNotFoundRunTime("Что-то пошло не так не удалось получить Календарь (api-calendar) не доступен подождите или обратитесь к администратору " + ex.getMessage());
        }
    }

    public PayDTO sendPay(PayDTO payDTO) {

        try {
            return webClient.post().uri("/send")
                    .bodyValue(payDTO)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                            cR -> getMessage(cR, "Что-то пошло не так не удалось получить отпуск за период"))
                    .bodyToMono(PayDTO.class)
                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .block();
        } catch (RuntimeException ex) {
            log.error("/send",ex);
            throw new ResourceNotFoundRunTime("Что-то пошло не так не удалось получить Календарь (api-pay) не доступен подождите или обратитесь к администратору " + ex.getMessage());
        }
    }

    public PayDTO getPay(Long id) throws ResourceNotFoundException {
        String uri = "/" + id;
        try {
            return webClient.get().uri(uri)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                            cR -> getMessage(cR, "Что-то пошло не так не удалось получить отпуск за период"))
                    .bodyToMono(PayDTO.class)
                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .block();
        } catch (RuntimeException ex) {
//            log.error(uri, ex);
            throw new ResourceNotFoundException("Что-то пошло не так не удалось получить платеж (api-pay) не доступен подождите или обратитесь к администратору " + ex.getMessage());
        }
    }

}
