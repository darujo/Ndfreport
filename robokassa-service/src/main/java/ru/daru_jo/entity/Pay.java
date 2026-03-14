package ru.daru_jo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "pay")
public class Pay {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "mag_code")
    private String magCode;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "description")
    private String description;
    @Column(name = "hash")
    private String hash;
    @Column(name = "email")
    private String email;
    @Column(name = "is_test")
    private boolean isTest;
    @Column(name = "is_test")
    private boolean isCompleted;
}
