package ru.daru_jo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import ru.daru_jo.properties.PayServiceProperty;

@Configuration
@PropertySource("classpath:integration.properties")
@EnableConfigurationProperties(
        PayServiceProperty.class
)
public class AppConfigPay extends WebClientConfig {
    private PayServiceProperty payServiceProperty;
    @Autowired
    public void setPayServiceProperty(PayServiceProperty payServiceProperty){
        this.payServiceProperty = payServiceProperty;
    }

    @Bean
    public WebClient webClientPay(){
        return webClient(payServiceProperty);
    }
}
