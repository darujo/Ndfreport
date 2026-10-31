package ru.daru_jo.service;

public class UrlService {
    // TODO сделать реальный адрес
    private static final String URL = "https://178.20.40.174:777/#!";
//    private static final String URL = "https://localhost:777/#!";

    public static String getUrl(String url, String name) {
        return String.format("<a href=\"%s\">%s</a>", url, name);
    }

    public static String getUrlNewUser(String nikName, String code) {
        return getUrl(URL + "/sys/email_confirm?code=" + code + "&nikName=" + nikName, "ссылке");
    }

    public static String getUrlRecovery(String nikName, String code) {
        return getUrl(URL + "/sys/recovery_password?code=" + code + "&nikName=" + nikName, "ссылке");
    }
}
