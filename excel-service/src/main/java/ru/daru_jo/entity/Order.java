package ru.daru_jo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "order_ndfl")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_nik")
    private String userNik;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "account")
    private String account;

    @Column(name = "year")
    private String year;

    public Order(String userNik) {
        this.userNik = userNik;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
