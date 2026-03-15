package ru.daru_jo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


@NoArgsConstructor
@Getter
public class OrderDTO {
    private Long id;

    private String userNik;

    private Timestamp timestamp;

    private List<String> yearList;

    private Boolean pay;


    public OrderDTO(Long id, String userNik, Timestamp timestamp, List<String> yearList, Boolean pay) {
        this.id = id;
        this.userNik = userNik;
        this.timestamp = timestamp;
        this.yearList = yearList;
        this.pay = pay;
    }
}
