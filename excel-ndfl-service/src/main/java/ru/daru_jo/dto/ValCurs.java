package ru.daru_jo.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ValCurs")
public class ValCurs {

    @XmlAttribute
    String name;

    @XmlAttribute(name = "Date")
    String date;

    @Getter
    @XmlElement(name = "Valute")
    private List<Valute> valuteList = new ArrayList<>();

    @SuppressWarnings("unused")
    public void setDate(String date) {
        this.date = date;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        int len = valuteList.size();
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append(String.format("%s %s %d ", name, date, len));
        for (Valute v : getValuteList()) {
            sb.append(v.toString());
        }
        sb.append(" ]");
        return sb.toString();
    }

}
