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
@Table(name = "val_curs",
        indexes = {
                @Index(columnList = "char_code,")})
public class CursVal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "num_code")
    private String numCode;

    @Column(name = "char_code")
    private String charCode;

    @Column(name = "nominal")
    private Long nominal;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private Double value;

    @Column(name = "val_unit_rate")
    private Double valUnitRate;

    @Column(name = "cbr_id")
    private String cbrId;

    @Column(name = "timestamp")
    private Timestamp timestamp;
}
