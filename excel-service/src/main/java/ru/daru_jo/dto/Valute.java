package ru.daru_jo.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;

@Getter
@XmlRootElement(name = "Valute")
public class Valute {

    @XmlAttribute(name = "ID")
    String id;

    @XmlElement(name = "NumCode")
    String numCode;

    @XmlElement(name = "CharCode")
    String charCode;

    @XmlElement(name = "Nominal")
    Long nominal;

    @XmlElement(name = "Name")
    String name;

    @XmlElement(name = "Value")
    @XmlJavaTypeAdapter(ValueAdapter.class)
    Double value;

    @XmlElement(name = "VunitRate")
    @XmlJavaTypeAdapter(ValueAdapter.class)
    Double valUnitRate;

    public String toString() {

        return String.format("[%s %s %s %s %s %s ]",
                id, numCode, charCode, nominal, name, value);

    }

}
