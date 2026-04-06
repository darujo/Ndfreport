package ru.daru_jo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordChangeDto implements Serializable {
    private String nikName;
    private String passwordOld;
    private String passwordNew;

}
