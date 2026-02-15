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
@Table(name = "expenditure")
public class Expenditure implements Movement {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category")
    private String category; // Asset Category (Класс актива)
    @Column(name = "type")
    private String type; // Asset Category (Класс актива)
    @Column(name = "currencyCode")
    private String currencyCode; //Currency (Валюта)
    @Column(name = "companyName")
    private String companyName; // Symbol (Символ)
    @Column(name = "timestamp")
    private Timestamp timestamp; // Date/Time (Дата/Время)
    @Column(name = "quantity")
    private Double quantity; // Quantity (Количество)
    @Column(name = "price")
    private Double price; // T. Price (Цена транзакции)
    @Column(name = "commission")
    private Double commission; // Comm/Fee (Комиссия/плата)
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
