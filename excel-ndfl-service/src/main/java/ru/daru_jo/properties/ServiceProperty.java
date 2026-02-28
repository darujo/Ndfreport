package ru.daru_jo.properties;

import lombok.Data;

@Data
public abstract class ServiceProperty implements PropertyConnectionInterface {
    private String url;
    private Integer connectionTimeOut;
    private Integer readTimeOut;
    private Integer writeTimeOut;
}
