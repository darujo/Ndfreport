package ru.daru_jo.dto;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ValueAdapter extends XmlAdapter<String, Double> {

//    private  NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

    @Override
    public Double unmarshal(String v)  {

        return  Double.valueOf(v.replace(",", "."));
    }

    @Override
    public String marshal(Double v) {
        return null;
    }

}
