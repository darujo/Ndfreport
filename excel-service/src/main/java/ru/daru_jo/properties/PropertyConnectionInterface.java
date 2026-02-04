package ru.daru_jo.properties;

public interface PropertyConnectionInterface {
    Integer getConnectionTimeOut();

    Integer getReadTimeOut();

    Integer getWriteTimeOut();

    String getUrl();
}
