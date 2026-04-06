package ru.daru_jo.api;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.daru_jo.convertor.UserConvertor;
import ru.daru_jo.dto.jwt.JwtResponse;
import ru.daru_jo.dto.user.UserDto;
import ru.daru_jo.dto.user.UserPasswordChangeDto;
import ru.daru_jo.service.AuthService;
import ru.daru_jo.service.UserService;
import ru.daru_jo.dto.user.UserEditDto;
import ru.daru_jo.dto.AttrDto;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private AuthService authService;

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/edit/{id}")
    public ResponseEntity<?> getUserEditDto(@PathVariable long id) {
        try {
            return ResponseEntity.ok(UserConvertor.getUserEditDto(userService.findById(id)));

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex);
        }
    }

    @PostMapping("/user/edit")
    public UserEditDto setUserEditDto(@RequestBody UserEditDto user) {
        return UserConvertor.getUserEditDto(
                userService.saveUser(
                        UserConvertor.getUser(user),
                        user.getTextPassword()));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDto(@RequestParam(required = false) String nikName) {
        try {
            return ResponseEntity.ok(UserConvertor.getUserDto(userService.loadUserByNikName(nikName)));

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex);
        }
    }

    @GetMapping("")
    public Page<@NonNull UserDto> getUserList(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size,
                                              @RequestParam(required = false) String nikName,
                                              @RequestParam(required = false) String lastName,
                                              @RequestParam(required = false) String firstName,
                                              @RequestParam(required = false) String patronymic
    ) {
        if (nikName != null) {
            if (nikName.equals("All")) {
                nikName = null;
            }
        }
        return userService.getUserList(page, size, nikName, lastName, firstName, patronymic).map(UserConvertor::getUserDto);
    }

    @PostMapping("/user/password/change")
    public boolean changePassword(@RequestBody UserPasswordChangeDto userPasswordChangeDto,
                                  @RequestHeader String username) {
        return userService.changePassword(username, userPasswordChangeDto.getPasswordOld(), userPasswordChangeDto.getPasswordNew());
    }

    @GetMapping("/user/password/hash")
    public AttrDto<?> getPasswordHash(@RequestParam String textPassword) {
        return new AttrDto<>("passwordHash", userService.hashPassword(textPassword));
    }

    @GetMapping("/user/password/check")
    public Boolean getPasswordCheck(@RequestParam String passwordText,
                                    @RequestParam String passwordHash) {
        return userService.checkPassword(passwordText, passwordHash);
    }

    @GetMapping("/user/password/recovery")
    public void getPasswordRecovery(@RequestParam String nikName,
                                    @RequestParam String email) {
        userService.getRestorePassword(nikName, email);
    }

    @GetMapping("/user/password/restore")
    public JwtResponse getPasswordRestore(@RequestParam String nikName,
                                          @RequestParam String code) {
        return authService.restorePassword(nikName, code);
    }

    @GetMapping("/user/Email/confirm")
    public boolean confirmEmail(String nikName, String code){
        return userService.confirmEmail(nikName,code);
    }
}
