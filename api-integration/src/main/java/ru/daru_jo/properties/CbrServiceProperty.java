package ru.daru_jo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "integration.cbr")
public class CbrServiceProperty extends ServiceProperty {
}
