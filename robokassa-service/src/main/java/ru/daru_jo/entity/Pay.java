package ru.daru_jo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "is_completed")
    private boolean isCompleted;

    public Pay(Long id, String magCode, Long orderId, Double amount, String description, String hash, String email, boolean isTest, boolean isCompleted) {
        this.id = id;
        this.magCode = magCode;
        this.orderId = orderId;
        this.amount = amount;
        this.description = description;
        this.hash = hash;
        this.email = email;
        this.isTest = isTest;
        this.isCompleted = isCompleted;
    }

    @Column(name = "fee")
    private Double fee;

    @Column(name = "hash_ok")
    private String hashOk;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "inc_curr_label")
    private String incCurrLabel;
}
