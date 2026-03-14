package ru.daru_jo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "integration.pay-service")
public class PayServiceProperty extends ServiceProperty {
}
