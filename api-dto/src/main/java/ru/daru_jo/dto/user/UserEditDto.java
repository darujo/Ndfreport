package ru.daru_jo.dto.user;

import lombok.Getter;

import java.io.Serializable;

public class UserEditDto implements Serializable {
    @SuppressWarnings("unused")
    public UserEditDto() {
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
    @Getter
    private String userPassword;
    @Getter
    private Boolean passwordChange;
    @Getter
    @SuppressWarnings("unused")
    private String textPassword;
    private Boolean block;

    @Getter
    private String email;

    public UserEditDto(Long id,
                       String nikName,
                       String firstName,
                       String lastName,
                       String patronymic,
                       String userPassword,
                       Boolean passwordChange,
                       Boolean block,
                       String email) {
        this.id = id;
        this.nikName = nikName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.userPassword = userPassword;
        this.passwordChange = passwordChange;
        this.block = block;
        this.email = email;
    }

    public Boolean isBlock() {
        return block;
    }

}
