package ru.daru_jo.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import ru.daru_jo.config.WebClientConfig;

@Configuration
@PropertySource("classpath:integration.properties")
@EnableConfigurationProperties(
        CbrServiceProperty.class
)
public class AppConfigCbr extends WebClientConfig {
    private CbrServiceProperty calendarServiceProperty;
    @Autowired
    public void setCalendarServiceProperty(CbrServiceProperty calendarServiceProperty){
        this.calendarServiceProperty = calendarServiceProperty;
    }

    @Bean
    public WebClient webClientCalendar(){
        return webClient(calendarServiceProperty);
    }
}
