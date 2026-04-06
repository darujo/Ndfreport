package ru.daru_jo.service;

public class UrlService {
    private static final String URL = "https://178.20.40.174:777/#!";

    public static String getUrl(String url) {
        return getUrl( url, url);
    }

    public static String getUrl(String url, String name) {
        return String.format("<a href=\"%s\">%s</a>", url, name);
    }

    public static String getUrlNewUser(String nikName, String code) {
        return getUrl(URL + "/email_confirm?code=" + code + "&nikName=" + nikName);
    }

    public static String getUrlRecovery(String nikName, String code) {
        return getUrl(URL + "/recovery_password?code=" + code + "&nikName=" + nikName);
    }
}
