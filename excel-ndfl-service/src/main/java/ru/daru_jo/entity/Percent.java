package ru.daru_jo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "percent")
public class Percent {
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
    @JoinColumn(name = "order_account_id")
    private OrderAccount orderAccount;
    @Column(name = "type")
    private String type;


    public Percent(Long id, String code, Timestamp date, Double amount, String currency, OrderAccount orderAccount, String type) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.orderAccount = orderAccount;
        this.type = type;
    }
}
