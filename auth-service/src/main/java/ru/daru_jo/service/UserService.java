package ru.daru_jo.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.daru_jo.dto.user.*;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;
import ru.daru_jo.hash.HashService;
import ru.daru_jo.model.User;
import ru.daru_jo.repository.UserRepository;
import ru.daru_jo.specifications.Specifications;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
public class UserService {
    @Value("${mail.secret}")
    private String codePas;
    @Getter
    private static UserService INSTANCE;
    private DefaultEmailService defaultEmailService;
    private UserRepository userRepository;


    public UserService() {
        INSTANCE = this;
    }


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setDefaultEmailService(DefaultEmailService defaultEmailService) {
        this.defaultEmailService = defaultEmailService;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Не найден пользователь c id " + id));
    }

    public Optional<User> findByNikName(String name) {
        return userRepository.findByNikNameIgnoreCase(name);
    }

    public void checkNull(String filed, String text) {
        if (filed == null || filed.isEmpty()) {
            throw new ResourceNotFoundRunTime("Не заполнено поле " + text);
        }
    }

    @Transactional
    public User saveUser(User user) {
        return saveUser(user, null);
    }

    @Transactional
    public User saveUser(User user, String textPassword) {
        checkNull(user.getNikName(), "логин");
        checkNull(user.getFirstName(), "имя");
        checkNull(user.getLastName(), "фамилия");


        if (textPassword != null && !textPassword.isEmpty()) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(hashPassword(textPassword));
            } else {
                if (!checkPassword(textPassword, user.getPassword())) {
                    throw new ResourceNotFoundRunTime("Пароль и хэш не совпадают");
                }
            }
        }
        boolean newEmail = false;
        if (user.getId() != null) {
            if (userRepository.findByNikNameIgnoreCaseAndIdIsNot(user.getNikName(), user.getId()).isPresent()) {
                throw new ResourceNotFoundRunTime("Уже есть пользователь с таким ником");
            }
            User finalUser = user;
            User userSave = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundRunTime("Нет пользователя с таким Id " + finalUser.getId()));
            if (user.getNewEmail() != null && !user.getNewEmail().equals(userSave.getNewEmail())) {
                setNewEmailCode(user);
                newEmail = true;
            } else {
                user.setCodeEmail(userSave.getCodeEmail());
                user.setSendCode(userSave.getSendCode());
                user.setEmail(userSave.getEmail());
                user.setRecovery(userSave.getRecovery());
            }
        } else {
            checkNull(user.getNewEmail(), "почта");
            if (userRepository.findByNikNameIgnoreCase(user.getNikName()).isPresent()) {
                throw new ResourceNotFoundRunTime("Уже есть пользователь с таким ником");
            }
            setNewEmailCode(user);
            newEmail = true;
        }
        user = userRepository.save(user);
        if(newEmail) {
            defaultEmailService.sendSimpleEmail(user.getNewEmail(), "Подтверждение почты", "Для подтверждения почты перейдите по ссылке" + UrlService.getUrlNewUser(user.getNikName(), getHash(user)));
        }
        return user;
    }

    private static void setNewEmailCode(User user) {
        int code = (int) ((99999999 * Math.random()));
        user.setCodeEmail(Integer.toString(code));
        user.setSendCode(new Timestamp(System.currentTimeMillis()));
        user.setEmail(user.getNewEmail());
        user.setRecovery(false);
    }

    @Transactional
    public User loadUserByNikName(String nikName) throws UsernameNotFoundException {
        // todo вынести в настройки
        if (nikName.equals("system_user_update")) {
            return new User(-1L, nikName, hashPassword(
                    "Приносить пользу миру — это единственный способ стать счастливым."),

                    null, null, null, false, false, null, null, null, null, null);
        }
        return findByNikName(nikName).orElseThrow(() -> new UsernameNotFoundException("Не найден пользователь по логину " + nikName));
    }

    @Transactional
    public Page<@NonNull User> getUserList(Integer page,
                                           Integer size,
                                           String nikName,
                                           String lastName,
                                           String firstName,
                                           String patronymic
    ) {
        Specification<@NonNull User> specification = getUserSpecification(nikName, lastName, firstName, patronymic);
        Sort sort = Sort.by("lastName")
                .and(Sort.by("firstName"));
        Page<@NonNull User> userPage;
        if (page == null) {
            userPage = new PageImpl<>(userRepository.findAll(specification, sort));
        } else {
            if (size == null || size < 1) {
                size = 10;
            }
            userPage = userRepository.findAll(specification, PageRequest.of(page - 1, size, sort));
        }
        return userPage;
    }

    private Specification<@NonNull User> getUserSpecification(String nikName, String lastName, String firstName, String patronymic) {
        Specification<@NonNull User> specification = Specification.unrestricted();

        specification = Specifications.like(specification, "nikName", nikName);
        specification = Specifications.like(specification, "lastName", lastName);
        specification = Specifications.like(specification, "firstName", firstName);
        specification = Specifications.like(specification, "patronymic", patronymic);
        return specification;
    }

    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainTextPassword, String hashPassword) {
        return BCrypt.checkpw(plainTextPassword, hashPassword);
    }

    @Transactional
    public boolean changePassword(String username, String passwordOld, String passwordNew) {
        User user = userRepository.findByNikNameIgnoreCase(username).orElseThrow(() -> new ResourceNotFoundRunTime("Пользователь не найден"));
        if (!checkPassword(passwordOld, user.getPassword())) {
            throw new ResourceNotFoundRunTime("Старый пароль не действителен");
        }

        if (passwordNew == null || passwordNew.isEmpty()) {
            throw new ResourceNotFoundRunTime("Новый пароль не должен быть пустым");
        }
        if (checkPassword(passwordNew, user.getPassword())) {
            throw new ResourceNotFoundRunTime("Новый пароль не должен совпадать со старым");
        }
        user.setPassword(hashPassword(passwordNew));
        user.setPasswordChange(false);
        user = saveUser(user);
        return user != null;
    }

    @Transactional
    public boolean confirmEmail(String nikName, String code) {
        User user = loadUserByNikName(nikName);
        if (code.equals(getHash(user))) {
            user.setCodeEmail(null);
            user.setEmail(user.getNewEmail());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public void getRestorePassword(String nikName, String email) {
        User user = loadUserByNikName(nikName);
        if (email != null && email.equals(user.getEmail())) {
            setNewEmailCode(user);
            userRepository.save(user);
            defaultEmailService.sendSimpleEmail(user.getEmail(), "Восстановление доступа", "Для восстановления пароля перейдите по ссылке" + UrlService.getUrlRecovery(user.getNikName(), getHash(user)));

        } else {
            throw new ResourceNotFoundRunTime("Почта не совпадает");
        }

    }
    @Transactional
    public boolean restorePassword(String nikName, String code) {
        User user = loadUserByNikName(nikName);
        if (code.equals(getHash(user))) {
            user.setCodeEmail(null);
            user.setPasswordChange(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String getHash(User user) {
        return HashService.getSHA256(user.hashCode() + ":" + codePas);
    }

}
