package ru.daru_jo.dto;

import lombok.Getter;

@Getter
public class AttrDto<T> {
    @SuppressWarnings("unused")
    public AttrDto() {
    }

    private  T codeT;
    private  String code;
    private  String value;

    public AttrDto(T codeT, String value) {
        this.codeT = codeT;
        this.code = codeT.toString();
        this.value = value;
    }
}
