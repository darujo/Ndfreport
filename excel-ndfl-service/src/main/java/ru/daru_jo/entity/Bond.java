package ru.daru_jo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "bond")
public class Bond {
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
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "price")
    private Double price;

    public Bond(Long id, String code, Timestamp date, Double amount, String currency, OrderAccount orderAccount, String type) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.orderAccount = orderAccount;
        this.type = type;
    }

    public Bond(Long id, String code, Timestamp date, String currency, OrderAccount orderAccount, String type, Double quantity) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.currency = currency;
        this.orderAccount = orderAccount;
        this.type = type;
        this.quantity = quantity;
        this.price = 1d;
        this.amount = price * quantity;
    }

    public Bond(Long id, String code, Timestamp date, Double amount, String currency, OrderAccount orderAccount, String type, Double quantity) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.orderAccount = orderAccount;
        this.type = type;
        this.quantity = quantity;
    }
}
