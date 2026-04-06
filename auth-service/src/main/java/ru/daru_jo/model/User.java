package ru.daru_jo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nik_name", unique = true)
    private String nikName;

    @Column(name = "password")
    private String password;

    @Column(name = "password_change")
    private Boolean passwordChange;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;


    @Column(name = "block", nullable = false)
    private boolean block;

    @Column(name = "code_email")
    private String codeEmail;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "email_new", nullable = false)
    private String newEmail;

    @Column(name = "send_code")
    private Timestamp sendCode;

    @Column(name = "recovery")
    private Boolean recovery;


    public User(Long id,
                String nikName,
                String password,
                String firstName,
                String lastName,
                String patronymic,
                Boolean passwordChange,
                boolean block,
                String codeEmail,
                String email,
                String newEmail,
                Timestamp sendCode,
                Boolean recovery
    ) {
        this.id = id;
        this.nikName = nikName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.passwordChange = passwordChange;
        this.block = block;
        this.codeEmail = codeEmail;
        this.email = email;
        this.newEmail = newEmail;
        this.sendCode = sendCode;
        this.recovery = recovery;
    }


}

