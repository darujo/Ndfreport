package ru.daru_jo.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.daru_jo.properties.ServiceProperty;

@ConfigurationProperties( prefix = "integration.cbr")
public class CbrServiceProperty extends ServiceProperty {
}
