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
@Table(name = "coupon_nkd_minus")
public class CouponNKDMinus {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code")
    private String code;
    @Column(name = "date")
    private Timestamp date;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "currency")
    private String currency;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // Comm/Fee (Комиссия/плата)



}
