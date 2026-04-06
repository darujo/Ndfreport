package ru.daru_jo.convertor;

import ru.daru_jo.dto.user.UserDto;
import ru.daru_jo.dto.user.UserEditDto;
import ru.daru_jo.model.User;

public class UserConvertor {
    public static UserDto getUserDto(User user) {
        return new UserDto(user.getId(),
                user.getNikName(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getPasswordChange(),
                user.isBlock()
        );
    }

    public static UserEditDto getUserEditDto(User user) {
        return new UserEditDto(user.getId(),
                user.getNikName(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getPassword(),
                user.getPasswordChange(),
                user.isBlock(),
                user.getEmail());
    }

    public static User getUser(UserEditDto user) {
        return new User(user.getId(),
                user.getNikName(),
                user.getUserPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getPasswordChange(),
                user.isBlock() != null && user.isBlock(),
                null,
                null,
                user.getEmail(),
                null,
                null);
    }
}
