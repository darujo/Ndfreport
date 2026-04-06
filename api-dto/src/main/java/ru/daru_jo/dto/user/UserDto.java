package ru.daru_jo.dto.user;

import lombok.Getter;

import java.io.Serializable;

public class UserDto implements Serializable {
    @SuppressWarnings("unused")
    public UserDto() {
    }

    @Getter
    private Long id;
    @Getter
    private String nikName;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Getter
    private String patronymic;
    private Boolean passwordChange;
    @Getter
    private boolean block;

    public UserDto(Long id, String nikName, String firstName, String lastName, String patronymic, Boolean passwordChange, boolean block) {
        this.id = id;
        this.nikName = nikName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.passwordChange = passwordChange;
        this.block = block;
    }

    @SuppressWarnings("unused")
    public Boolean getPasswordChange() {
        return passwordChange;
    }

}
